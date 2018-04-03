package valandur.webapi.integration.redprotect;

import br.net.fabiozumbi12.RedProtect.Sponge.Region;
import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.player.ICachedPlayerFull;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.util.*;

@ApiModel("RedProtectRegion")
public class CachedRegion extends CachedObject<Region> {

    private String id;
    @ApiModelProperty(value = "The unique id of this region", required = true)
    public String getId() {
        return id;
    }

    private String name;
    @ApiModelProperty(value = "The name of this region", required = true)
    public String getName() {
        return name;
    }

    private ICachedWorld world;
    @JsonDetails(value = false, simple = true)
    @ApiModelProperty(value = "The world this region is located in", required = true)
    public ICachedWorld getWorld() {
        return world;
    }

    private Vector3d min;
    @ApiModelProperty(value = "The minimum coordinates that define the region", required = true)
    public Vector3d getMin() {
        return min;
    }

    private Vector3d max;
    @ApiModelProperty(value = "The maximum coordinates that define the region", required = true)
    public Vector3d getMax() {
        return max;
    }

    private Integer priority;
    @JsonDetails
    @ApiModelProperty("The priority of this region compared to other regions")
    public Integer getPriority() {
        return priority;
    }

    private Boolean canDelete;
    @JsonDetails
    @ApiModelProperty("True if this region can be deleted, false otherwise")
    public Boolean getCanDelete() {
        return canDelete;
    }

    private List<ICachedPlayer> leaders;
    @JsonDetails(simple = true)
    @ApiModelProperty("A list of players that are leaders of this region")
    public List<ICachedPlayer> getLeaders() {
        return leaders;
    }

    private List<ICachedPlayer> admins;
    @JsonDetails(simple = true)
    @ApiModelProperty("A list of players that are admins of this region")
    public List<ICachedPlayer> getAdmins() {
        return admins;
    }

    private List<ICachedPlayer> members;
    @JsonDetails(simple = true)
    @ApiModelProperty("A list of players that are members of this region")
    public List<ICachedPlayer> getMembers() {
        return members;
    }

    private String welcomeMessage;
    @JsonDetails
    @ApiModelProperty("The welcome message displayed to a player when they enter this region")
    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    private String date;
    @JsonDetails
    @ApiModelProperty("The date this region was created?")
    public String getDate() {
        return date;
    }

    private HashMap<String, Object> flags;
    @JsonDetails
    @ApiModelProperty("A map of flags applicable to this region")
    public HashMap<String, Object> getFlags() {
        return flags;
    }

    private CachedLocation tpPoint;
    @JsonDetails
    @ApiModelProperty("The teleport point for this region")
    public CachedLocation getTpPoint() {
        return tpPoint;
    }


    public CachedRegion() {
        super(null);
    }
    public CachedRegion(Region region) {
        super(region);

        this.id = region.getID();
        this.name = region.getName();
        this.world = cacheService.getWorld(region.getWorld()).orElse(null);
        this.min = new Vector3d(region.getMinMbrX(), region.getMinY(), region.getMinMbrZ());
        this.max = new Vector3d(region.getMaxMbrX(), region.getMaxY(), region.getMaxMbrZ());
        this.priority = region.getPrior();
        this.welcomeMessage = region.getWelcome();
        this.date = region.getDate();
        this.canDelete = region.canDelete();
        this.tpPoint = region.getTPPoint() != null ? new CachedLocation(region.getTPPoint()) : null;

        this.leaders = new ArrayList<>();
        for (String uuid : region.getLeaders()) {
            Optional<ICachedPlayerFull> optPlayer = cacheService.getPlayer(uuid);
            optPlayer.ifPresent(player -> this.leaders.add(player));
        }

        this.admins = new ArrayList<>();
        for (String uuid : region.getAdmins()) {
            Optional<ICachedPlayerFull> optPlayer = cacheService.getPlayer(uuid);
            optPlayer.ifPresent(player -> this.admins.add(player));
        }

        this.members = new ArrayList<>();
        for (String uuid : region.getLeaders()) {
            Optional<ICachedPlayerFull> optPlayer = cacheService.getPlayer(uuid);
            optPlayer.ifPresent(player -> this.members.add(player));
        }

        this.flags = new HashMap<>(region.flags.size());
        for (Map.Entry<String, Object> entry : region.flags.entrySet()) {
            this.flags.put(entry.getKey(), cacheService.asCachedObject(entry.getValue()));
        }
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/red-protect/region/" + id;
    }
}
