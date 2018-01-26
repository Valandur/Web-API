package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.serialize.request.misc.DamageRequest;
import valandur.webapi.serialize.request.player.UpdatePlayerRequest;
import valandur.webapi.servlet.base.ServletData;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Servlet(basePath = "player")
public class PlayerServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void getPlayers(ServletData data) {
        data.addData("ok", true, false);
        data.addData("players", cacheService.getPlayers(), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "/:player", perm = "one")
    public void getPlayer(ServletData data, CachedPlayer player) {
        Optional<String> strFields = data.getQueryParam("fields");
        Optional<String> strMethods = data.getQueryParam("methods");
        if (strFields.isPresent() || strMethods.isPresent()) {
            String[] fields = strFields.map(s -> s.split(",")).orElse(new String[]{});
            String[] methods = strMethods.map(s -> s.split(",")).orElse(new String[]{});
            Tuple extra = cacheService.getExtraData(player, data.responseIsXml(), fields, methods);
            data.addData("fields", extra.getFirst(), true);
            data.addData("methods", extra.getSecond(), true);
        }

        data.addData("ok", true, false);
        data.addData("player", player, true);
    }

    @Endpoint(method = HttpMethod.PUT, path = "/:player", perm = "change")
    public void updatePlayer(ServletData data, CachedPlayer player) {
        Optional<UpdatePlayerRequest> optReq = data.getRequestBody(UpdatePlayerRequest.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid player data: " + data.getLastParseError().getMessage());
            return;
        }

        final UpdatePlayerRequest req = optReq.get();

        Optional<ICachedPlayer> resPlayer = WebAPI.runOnMain(() -> {
            Optional<Player> optLive = player.getLive();
            if (!optLive.isPresent())
                return null;

            Player live = optLive.get();

            if (req.getWorld().isPresent()) {
                Optional<World> optWorld = req.getWorld().get().getLive();
                if (!optWorld.isPresent())
                    return null;

                if (req.getPosition() != null) {
                    live.transferToWorld(optWorld.get(), req.getPosition());
                } else {
                    live.transferToWorld(optWorld.get());
                }
            } else if (req.getPosition() != null) {
                live.setLocation(new Location<>(live.getWorld(), req.getPosition()));
            }
            if (req.getVelocity() != null) {
                live.setVelocity(req.getVelocity());
            }
            if (req.getRotation() != null) {
                live.setRotation(req.getRotation());
            }

            if (req.getScale() != null) {
                live.setRotation(req.getScale());
            }

            if (req.getFoodLevel() != null) {
                live.getFoodData().foodLevel().set(req.getFoodLevel());
            }
            if (req.getExhaustion() != null) {
                live.getFoodData().exhaustion().set(req.getExhaustion());
            }
            if (req.getSaturation() != null) {
                live.getFoodData().saturation().set(req.getSaturation());
            }

            if (req.getTotalExperience() != null) {
                live.get(ExperienceHolderData.class).map(exp -> exp.totalExperience().set(req.getTotalExperience()));
            }
            if (req.getLevel() != null) {
                live.get(ExperienceHolderData.class).map(exp -> exp.level().set(req.getLevel()));
            }
            if (req.getExperienceSinceLevel() != null) {
                live.get(ExperienceHolderData.class).map(exp -> exp.experienceSinceLevel().set(req.getExperienceSinceLevel()));
            }

            if (req.getHealth() != null) {
                live.get(HealthData.class).map(h -> h.health().set(req.getHealth()));
            }
            if (req.getMaxHealth() != null) {
                live.get(HealthData.class).map(h -> h.maxHealth().set(req.getMaxHealth()));
            }

            if (req.getDamage() != null) {
                DamageRequest dmgReq = req.getDamage();
                DamageSource.Builder builder = DamageSource.builder();
                if (dmgReq.getDamageType().isPresent())
                    builder.type(dmgReq.getDamageType().get());

                live.damage(req.getDamage().getAmount(), builder.build());
            }

            if (req.hasInventory()) {
                try {
                    Inventory inv = ((Carrier) live).getInventory();
                    inv.clear();
                    for (ItemStack stack : req.getInventory()) {
                        inv.offer(stack);
                    }
                } catch (Exception e) {
                    return null;
                }
            }

            return cacheService.updatePlayer(live);
        });

        data.addData("ok", resPlayer.isPresent(), false);
        data.addData("player", resPlayer.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "/:player/method", perm = "method")
    public void executeMethod(ServletData data) {
        String uuid = data.getPathParam("player");
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid player UUID");
            return;
        }

        Optional<ICachedPlayer> player = cacheService.getPlayer(UUID.fromString(uuid));
        if (!player.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Player with UUID '" + uuid + "' could not be found");
            return;
        }

        final JsonNode reqJson = data.getRequestBody();
        if (!reqJson.has("method")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request must define the 'method' property");
            return;
        }

        String mName = reqJson.get("method").asText();
        Optional<Tuple<Class[], Object[]>> params = Util.parseParams(reqJson.get("params"));

        if (!params.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
            return;
        }

        Optional<Object> res = cacheService.executeMethod(player.get(), mName, params.get().getFirst(), params.get().getSecond());
        if (!res.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get world");
            return;
        }

        data.addData("ok", true, false);
        data.addData("player", player.get(), true);
        data.addData("result", res.get(), true);
    }
}
