package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.crate.VirtualCrate;
import com.codehusky.huskycrates.crate.config.CrateReward;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CachedCrate {

    private String id;
    public String getId() {
        return id;
    }

    private String name;
    public String getName() {
        return name;
    }

    private String type;
    public String getType() {
        return type;
    }

    private boolean isFree;
    public boolean isFree() {
        return isFree;
    }

    private Map<String, Integer> keys;
    public Map<String, Integer> getKeys() {
        return keys;
    }

    private List<CachedCrateReward> rewards;
    public List<CachedCrateReward> getRewards() {
        return rewards;
    }


    public CachedCrate(VirtualCrate crate) {
        this.id = crate.id;
        this.name = crate.displayName;
        this.type = crate.crateType;
        this.isFree = crate.freeCrate;
        this.keys = crate.pendingKeys;
        this.rewards = crate.getItemSet().stream()
                .map(o -> new CachedCrateReward((CrateReward)o[1]))
                .collect(Collectors.toList());
    }
}
