package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.MobSpawnerData;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.util.weighted.WeightedTable;
import valandur.webapi.api.serialize.BaseView;

public class MobSpawnerDataView extends BaseView<MobSpawnerData> {

    public short maximumNearbyEntities;
    public short maximumSpawnDelay;
    public short minimumSpawnDelay;
    public EntityArchetype nextEntityToSpawn;
    public WeightedTable<EntityArchetype> possibleEntitiesToSpawn;
    public short remainingDelay;
    public short requiredPlayerRange;
    public short spawnCount;
    public short spawnRange;


    public MobSpawnerDataView(MobSpawnerData value) {
        super(value);

        this.maximumNearbyEntities = value.maximumNearbyEntities().get();
        this.maximumSpawnDelay = value.maximumSpawnDelay().get();
        this.minimumSpawnDelay = value.minimumSpawnDelay().get();
        this.nextEntityToSpawn = value.nextEntityToSpawn().get().get();
        this.possibleEntitiesToSpawn = value.possibleEntitiesToSpawn().getAll();
        this.remainingDelay = value.remainingDelay().get();
        this.requiredPlayerRange = value.requiredPlayerRange().get();
        this.spawnCount = value.spawnCount().get();
        this.spawnRange = value.spawnRange().get();
    }
}
