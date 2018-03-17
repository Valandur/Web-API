package valandur.webapi.servlet;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.config.MapConfig;
import valandur.webapi.util.Util;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Path("map")
@Api(tags = { "Map" }, value = "Get maps of the biomes for each world on the server")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class MapServlet extends BaseServlet {

    private static int TILE_SIZE = 512;
    private static int HALF_TILE_SIZE = TILE_SIZE / 2;
    private Map<String, String> biomeColorMap = new ConcurrentHashMap<>();


    public MapServlet() {
        biomeColorMap.clear();

        MapConfig config = Util.loadConfig("map.conf", new MapConfig());
        biomeColorMap.putAll(config.biomeColors);
    }

    @GET
    @Path("/{world}/{x}/{z}")
    @Permission("map")
    @Produces("image/png")
    @ApiOperation(
            value = "Get a map tile",
            notes = "Returns an image representing the biomes of the blocks within the specified tile")
    public Response getMap(
            @PathParam("world") @ApiParam("The world to get the map tile from") CachedWorld world,
            @PathParam("x") @ApiParam("The x-coordinate of the tile (is multiplied by the TILE_SIZE)") int x,
            @PathParam("z") @ApiParam("The z-coordinate of the tile (is multiplied by the TILE_SIZE)") int z) {
        int bX = TILE_SIZE * x;
        int bZ = TILE_SIZE * z;
        Vector3i min = new Vector3i(bX - HALF_TILE_SIZE, 0, bZ - HALF_TILE_SIZE);
        Vector3i max = new Vector3i(bX + HALF_TILE_SIZE, 0, bZ + HALF_TILE_SIZE);

        String fileName = "tile-x" + x + "z" + z + ".png";
        java.nio.file.Path filePath = Paths.get("webapi/cache/" + world.getUUID() + "/" + fileName);
        if (!filePath.getParent().toFile().exists())
            filePath.getParent().toFile().mkdirs();

        CacheControl cc = new CacheControl();
        cc.setMaxAge(31536000);

        if (Files.exists(filePath)) {
            return Response.ok((StreamingOutput) output -> {
                BufferedImage img = ImageIO.read(filePath.toFile());
                ImageIO.write(img, "PNG", output);
            }).cacheControl(cc).build();
        }

        String[][] biomes = blockService.getBiomes(world, min, max);
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
        } catch (IOException e) {
            throw new InternalServerErrorException("Could not save tile image");
        }

        return Response.ok((StreamingOutput) output -> {
            ImageIO.write(img, "PNG", output);
        }).cacheControl(cc).build();
    }
}
