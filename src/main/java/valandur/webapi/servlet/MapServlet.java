package valandur.webapi.servlet;

import com.flowpowered.math.vector.Vector3i;
import ninja.leaping.configurate.ConfigurationNode;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.servlet.base.ServletData;
import valandur.webapi.util.Util;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Servlet(basePath = "map")
public class MapServlet extends BaseServlet {

    private static int TILE_SIZE = 512;
    private static int HALF_TILE_SIZE = TILE_SIZE / 2;
    private Map<String, String> biomeColorMap = new HashMap<>();


    public MapServlet() {
        biomeColorMap.clear();

        ConfigurationNode config = Util.loadWithDefaults("map.conf", "defaults/map.conf").getSecond();

        ConfigurationNode biomeColors = config.getNode("biomeColors");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : biomeColors.getChildrenMap().entrySet()) {
            biomeColorMap.put(entry.getKey().toString(), entry.getValue().getString());
        }
    }

    @Endpoint(method = HttpMethod.GET, path = "/:world/:x/:z", perm = "map")
    public void getMap(ServletData data, CachedWorld world, int x, int z) {
        int bX = TILE_SIZE * x;
        int bZ = TILE_SIZE * z;
        Vector3i min = new Vector3i(bX - HALF_TILE_SIZE, 0, bZ - HALF_TILE_SIZE);
        Vector3i max = new Vector3i(bX + HALF_TILE_SIZE, 0, bZ + HALF_TILE_SIZE);

        String fileName = "tile-x" + x + "z" + z + ".png";
        Path filePath = Paths.get("webapi/cache/" + world.getUUID() + "/" + fileName);
        if (!filePath.getParent().toFile().exists())
            filePath.getParent().toFile().mkdirs();

        if (Files.exists(filePath)) {
            try {
                Files.copy(filePath, data.getOutputStream());
                data.setHeader("Cache-Control", "public, max-age=31536000");
                data.setContentType("image/png");
                data.setDone();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Optional<String[][]> optBiomes = blockService.getBiomes(world, min, max);
        if (!optBiomes.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get biomes");
            return;
        }

        String[][] biomes = optBiomes.get();
        BufferedImage img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[i].length; j++) {
                String biome = biomes[i][j];
                String hexColor = biomeColorMap.get(biome);

                if (hexColor == null)
                    hexColor = biomeColorMap.get(biome.replace("mutated_", ""));

                if (hexColor == null) {
                    WebAPI.getLogger().info("No color for biome: " + biome + ". You can set one in the map.conf file");

                    hexColor = "FFFFFF";
                    biomeColorMap.put(biome, hexColor);
                }

                g2.setColor(Color.decode("#" + hexColor));
                g2.fillRect(i * 4, img.getHeight() - j * 4, 4, 4);
            }
        }

        g2.dispose();

        try {
            ImageIO.write(img, "PNG", new File(filePath.toString()));
            ImageIO.write(img, "PNG", data.getOutputStream());
            img.flush();

            data.setHeader("Cache-Control", "public, max-age=31536000");
            data.setContentType("image/png");
            data.setDone();
        } catch (IOException ignored) {}
    }
}
