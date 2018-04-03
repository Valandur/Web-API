package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.MobSpawnerData;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.util.weighted.WeightedTable;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("MobSpawnerData")
public class MobSpawnerDataView extends BaseView<MobSpawnerData> {

    @ApiModelProperty(value = "The maximum number of nearby entities for another mob to spawn", required = true)
    public short maximumNearbyEntities;

    @ApiModelProperty(value = "The maximum delay between two consecutive spawns", required = true)
    public short maximumSpawnDelay;

    @ApiModelProperty(value = "The minimum delay between two consecutive spawns", required = true)
    public short minimumSpawnDelay;

    @ApiModelProperty(value = "The next entity type that will be spawned by this spawner", required = true)
    public EntitySnapshot nextEntityToSpawn;

    @ApiModelProperty(value = "A weighted table of probability for each entity type to spawn", required = true)
    public WeightedTable<EntitySnapshot> possibleEntitiesToSpawn;

    @ApiModelProperty(value = "The remaining time until the next spawn attempt", required = true)
    public short remainingDelay;

    @ApiModelProperty(value = "The block range within there must be a player to trigger the spawn", required = true)
    public short requiredPlayerRange;

    @ApiModelProperty(value = "The amount of entities that will spawn in one attempt", required = true)
    public short spawnCount;

    @ApiModelProperty(value = "The range from the spawner within which the entities will spawn", required = true)
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
