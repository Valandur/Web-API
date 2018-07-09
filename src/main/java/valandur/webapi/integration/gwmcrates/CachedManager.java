package valandur.webapi.integration.gwmcrates;

import org.gwmdevelopments.sponge_plugin.crates.manager.Manager;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.serialize.JsonDetails;

import java.util.List;
import java.util.stream.Collectors;

public class CachedManager extends CachedObject<Manager> {

    private String id;
    public String getId() {
        return id;
    }

    private String name;
    public String getName() {
        return name;
    }

    private CachedCase caze;
    @JsonDetails
    public CachedCase getCase() {
        return caze;
    }

    private CachedKey key;
    @JsonDetails
    public CachedKey getKey() {
        return key;
    }

    private CachedOpenManager openManager;
    @JsonDetails
    public CachedOpenManager getOpenManager() {
        return openManager;
    }

    private CachedPreview preview;
    @JsonDetails
    public CachedPreview getPreview() {
        return preview;
    }

    private List<CachedDrop> drops;
    @JsonDetails
    public List<CachedDrop> getDrops() {
        return drops;
    }


    public CachedManager(Manager value) {
        super(value);

        this.id = value.getId();
        this.name = value.getName();
        this.caze = new CachedCase(value.getCase());
        this.key = new CachedKey(value.getKey());
        this.openManager = new CachedOpenManager(value.getOpenManager());
        this.preview = value.getPreview().map(CachedPreview::new).orElse(null);
        this.drops = value.getDrops().stream().map(CachedDrop::new).collect(Collectors.toList());
    }
}
