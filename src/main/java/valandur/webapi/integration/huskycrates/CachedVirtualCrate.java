package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.crate.VirtualCrate;
import com.codehusky.huskycrates.crate.config.CrateReward;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonDeserialize
public class CachedVirtualCrate extends CachedObject<VirtualCrate> {

    @JsonDeserialize
    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @JsonDeserialize
    private String name;
    public String getName() {
        return name;
    }

    @JsonDeserialize
    private String type;
    public String getType() {
        return type;
    }

    @JsonDeserialize
    @JsonProperty("free")
    private boolean isFree;
    @JsonDetails
    public boolean isFree() {
        return isFree;
    }

    @JsonDeserialize
    private Integer freeDelay;
    @JsonDetails
    public Integer getFreeDelay() {
        return freeDelay;
    }

    @JsonDeserialize
    private boolean scrambleRewards;
    @JsonDetails
    public boolean isScrambleRewards() {
        return scrambleRewards;
    }

    @JsonIgnore
    private Map<String, Integer> keys;
    @JsonDetails
    public Map<String, Integer> getKeys() {
        return keys;
    }

    @JsonDeserialize
    private List<CachedCrateReward> rewards;
    @JsonDetails
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
        this.isFree = crate.freeCrate;
        this.freeDelay = (Integer)crate.getOptions().get("freeCrateDelay");
        this.keys = crate.pendingKeys;
        this.rewards = crate.getItemSet().stream()
                .map(o -> new CachedCrateReward((CrateReward)o[1]))
                .collect(Collectors.toList());
    }
}
