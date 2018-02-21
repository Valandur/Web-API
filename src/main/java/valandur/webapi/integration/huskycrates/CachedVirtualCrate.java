package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.crate.VirtualCrate;
import com.codehusky.huskycrates.crate.config.CrateReward;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApiModel("HuskyCratesCrate")
public class CachedVirtualCrate extends CachedObject<VirtualCrate> {

    private String id;
    @ApiModelProperty(value = "The unique id of this crate", required = true)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    private String name;
    @ApiModelProperty(value = "The name of the crate", required = true)
    public String getName() {
        return name;
    }

    private String type;
    @ApiModelProperty(
            value = "The type of crate",
            allowableValues = "Spinner, Roulette, Instant, Simple",
            required = true)
    public String getType() {
        return type;
    }

    private boolean free;
    @JsonDetails
    @ApiModelProperty("True if this crate is free to open, false otherwise")
    public boolean isFree() {
        return free;
    }

    private Integer freeDelay;
    @JsonDetails
    @ApiModelProperty("In case this crate is free, this interval specifies the time (in seconds) after which " +
            "this crate can be opened again")
    public Integer getFreeDelay() {
        return freeDelay;
    }

    private boolean scrambleRewards;
    @JsonDetails
    @ApiModelProperty("True if the rewards are scrambled, false otherwise")
    public boolean isScrambleRewards() {
        return scrambleRewards;
    }

    @JsonIgnore
    private Map<String, Integer> keys;
    @JsonDetails
    @ApiModelProperty("A map from currently pending player UUIDs to keys")
    public Map<String, Integer> getKeys() {
        return keys;
    }

    private List<CachedCrateReward> rewards;
    @JsonDetails
    @ApiModelProperty("The possible rewards awarded for opening this crate")
    public List<CachedCrateReward> getRewards() {
        return rewards;
    }


    public CachedVirtualCrate() {
        super(null);
    }
    public CachedVirtualCrate(VirtualCrate crate) {
        super(crate);

        this.id = crate.id;
        this.name = crate.displayName;
        this.type = crate.crateType;
        this.free = crate.freeCrate;
        this.freeDelay = (Integer)crate.getOptions().get("freeCrateDelay");
        this.keys = crate.pendingKeys;
        this.rewards = crate.getItemSet().stream()
                .map(o -> new CachedCrateReward((CrateReward)o[1]))
                .collect(Collectors.toList());
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/husky-crates/crate/" + id;
    }
}
