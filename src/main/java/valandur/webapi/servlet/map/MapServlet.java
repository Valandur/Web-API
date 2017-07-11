package valandur.webapi.servlet.map;

import com.flowpowered.math.vector.Vector3i;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.servlet.ServletData;

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

@WebAPIServlet(basePath = "map")
public class MapServlet extends WebAPIBaseServlet {

    private static int TILE_SIZE = 512;
    private static int HALF_TILE_SIZE = TILE_SIZE / 2;
    private Map<String, String> biomeColorMap = new HashMap<>();


    public MapServlet() {
        biomeColorMap.put("minecraft:ocean", "000070");
        biomeColorMap.put("minecraft:plains", "8DB360");
        biomeColorMap.put("minecraft:desert", "FA9418");
        biomeColorMap.put("minecraft:extreme_hills", "606060");
        biomeColorMap.put("minecraft:forest", "056621");
        biomeColorMap.put("minecraft:taiga", "0B6659");
        biomeColorMap.put("minecraft:swampland", "07F9B2");
        biomeColorMap.put("minecraft:river", "0000FF");
        biomeColorMap.put("minecraft:hell", "FF0000");
        biomeColorMap.put("minecraft:sky", "8080FF");
        biomeColorMap.put("minecraft:frozen_ocean", "9090A0");
        biomeColorMap.put("minecraft:frozen_river", "A0A0FF");
        biomeColorMap.put("minecraft:ice_flats", "FFFFFF");
        biomeColorMap.put("minecraft:ice_mountains", "A0A0A0");
        biomeColorMap.put("minecraft:mushroom_island", "FF00FF");
        biomeColorMap.put("minecraft:mushroom_island_shore", "A000FF");
        biomeColorMap.put("minecraft:beaches", "FADE55");
        biomeColorMap.put("minecraft:desert_hills", "D25F12");
        biomeColorMap.put("minecraft:forest_hills", "22551C");
        biomeColorMap.put("minecraft:taiga_hills", "163933");
        biomeColorMap.put("minecraft:smaller_extreme_hills", "72789A");
        biomeColorMap.put("minecraft:jungle", "537B09");
        biomeColorMap.put("minecraft:jungle_hills", "2C4205");
        biomeColorMap.put("minecraft:jungle_edge", "628B17");
        biomeColorMap.put("minecraft:deep_ocean", "000030");
        biomeColorMap.put("minecraft:stone_beach", "A2A284");
        biomeColorMap.put("minecraft:cold_beach", "FAF0C0");
        biomeColorMap.put("minecraft:birch_forest", "307444");
        biomeColorMap.put("minecraft:birch_forest_hills", "1F5F32");
        biomeColorMap.put("minecraft:roofed_forest", "40511A");
        biomeColorMap.put("minecraft:taiga_cold", "31554A");
        biomeColorMap.put("minecraft:taiga_cold_hills", "243F36");
        biomeColorMap.put("minecraft:redwood_taiga", "596651");
        biomeColorMap.put("minecraft:redwood_taiga_hills", "545F3E");
        biomeColorMap.put("minecraft:extreme_hills_with_trees", "507050");
        biomeColorMap.put("minecraft:savanna", "BDB25F");
        biomeColorMap.put("minecraft:savanna_rock", "A79D64");
        biomeColorMap.put("minecraft:mesa", "D94515");
        biomeColorMap.put("minecraft:mesa_rock", "B09765");
        biomeColorMap.put("minecraft:mesa_clear_rock", "CA8C65");
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:world/:x/:z", perm = "map")
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
                String biome = biomes[i][j].replace("mutated_", "");

                String hexColor = biomeColorMap.get(biome);
                if (hexColor == null) {
                    WebAPI.getLogger().info("Unkown biome: " + biome);

                    hexColor = "FFFFFF";
                    biomeColorMap.put(biome, hexColor);
                }

                g2.setColor(Color.decode("#" + hexColor));
                g2.fillRect(i * 4, img.getHeight() - j * 4, 4, 4);
            }
        }

        try {
            ImageIO.write(img, "PNG", new File(filePath.toString()));
            ImageIO.write(img, "PNG", data.getOutputStream());

            data.setContentType("image/png");
            data.setDone();
        } catch (IOException ignored) {}
    }
}
