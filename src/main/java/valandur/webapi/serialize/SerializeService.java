package valandur.webapi.serialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.mutable.*;
import org.spongepowered.api.data.manipulator.mutable.block.*;
import org.spongepowered.api.data.manipulator.mutable.entity.*;
import org.spongepowered.api.data.manipulator.mutable.item.*;
import org.spongepowered.api.data.manipulator.mutable.tileentity.*;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.property.AbstractProperty;
import org.spongepowered.api.data.property.block.*;
import org.spongepowered.api.data.property.entity.DominantHandProperty;
import org.spongepowered.api.data.property.entity.EyeHeightProperty;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.data.property.item.*;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.extra.fluid.FluidStack;
import org.spongepowered.api.extra.fluid.FluidStackSnapshot;
import org.spongepowered.api.extra.fluid.data.manipulator.mutable.FluidItemData;
import org.spongepowered.api.extra.fluid.data.manipulator.mutable.FluidTankData;
import org.spongepowered.api.extra.fluid.data.property.FluidTemperatureProperty;
import org.spongepowered.api.extra.fluid.data.property.FluidViscosityProperty;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.*;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.weighted.RandomObjectTable;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.explosion.Explosion;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.command.CachedCommand;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedCause;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.*;
import valandur.webapi.serialize.deserialize.*;
import valandur.webapi.serialize.view.block.BlockSnapshotView;
import valandur.webapi.serialize.view.block.BlockStateView;
import valandur.webapi.serialize.view.data.*;
import valandur.webapi.serialize.view.economy.AccountView;
import valandur.webapi.serialize.view.economy.CurrencyView;
import valandur.webapi.serialize.view.entity.CareerView;
import valandur.webapi.serialize.view.entity.EntityArchetypeView;
import valandur.webapi.serialize.view.entity.EntitySnapshotView;
import valandur.webapi.serialize.view.entity.TradeOfferView;
import valandur.webapi.serialize.view.event.DamageSourceView;
import valandur.webapi.serialize.view.event.EventView;
import valandur.webapi.serialize.view.fluid.FluidStackSnapshotView;
import valandur.webapi.serialize.view.fluid.FluidStackView;
import valandur.webapi.serialize.view.item.*;
import valandur.webapi.serialize.view.misc.*;
import valandur.webapi.serialize.view.permission.SubjectCollectionView;
import valandur.webapi.serialize.view.permission.SubjectView;
import valandur.webapi.serialize.view.player.*;
import valandur.webapi.serialize.view.tileentity.PatternLayerView;
import valandur.webapi.util.TreeNode;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The serialize service is used to convert java objects into json/xml.
 * You can register your own serializers here to determine how certain objects are converted into json.
 */
public class SerializeService {

    private Map<Class, BaseSerializer> serializers;
    private Map<String, Class<? extends DataManipulator<?, ?>>> supportedData;
    private Map<Class<? extends Property<?, ?>>, String> supportedProperties;


    public void init() {
        Logger logger = WebAPI.getLogger();

        logger.info("Loading serializers...");

        serializers = new ConcurrentHashMap<>();

        // Cached Objects
        _register(CatalogType.class, CachedCatalogType.class);
        registerCache(Cause.class, CachedCause.class);
        registerCache(Chunk.class, CachedChunk.class);
        registerCache(CommandMapping.class, CachedCommand.class);
        registerCache(Entity.class, CachedEntity.class);
        registerCache(Inventory.class, CachedInventory.class);
        registerCache(Location.class, CachedLocation.class);
        registerCache(Player.class, CachedPlayer.class);
        registerCache(PluginContainer.class, CachedPluginContainer.class);
        //registerCache(PluginDependency.class, CachedPluginDependency.class);
        registerCache(TileEntity.class, CachedTileEntity.class);
        registerCache(Transform.class, CachedTransform.class);
        registerCache(World.class, CachedWorld.class);
        registerCache(WorldBorder.class, CachedWorldBorder.class);

        // Block
        registerView(BlockSnapshot.class, BlockSnapshotView.class);
        registerView(BlockState.class, BlockStateView.class);

        // Data
        registerView(ListData.class, ListDataView.class);
        registerView(MappedData.class, MappedDataView.class);
        registerView(VariantData.class, VariantDataView.class);

        //registerView(AbsorptionData.class, AbsorptionDataView.class);
        registerView(AchievementData.class, AchievementDataView.class);
        registerView(AgeableData.class, AgeableDataView.class);
        registerView(AgentData.class, AgentDataView.class);
        registerView(AggressiveData.class, AggressiveDataView.class);
        registerView(AngerableData.class, AngerableDataView.class);
        registerView(ArmorStandData.class, ArmorStandDataView.class);
        registerView(AttachedData.class, AttachedDataView.class);
        registerView(AuthorData.class, AuthorDataView.class);
        registerView(BannerData.class, BannerDataView.class);
        registerView(BeaconData.class, BeaconDataView.class);
        registerView(BlockItemData.class, BlockItemDataView.class);
        registerView(BreathingData.class, BreathingDataView.class);
        registerView(BreedableData.class, BreedableDataView.class);
        registerView(BrewingStandData.class, BrewingStandDataView.class);
        registerView(ChargedData.class, ChargedDataView.class);
        registerView(ColoredData.class, ColoredDataView.class);
        registerView(CommandData.class, CommandDataView.class);
        registerView(ConnectedDirectionData.class, ConnectedDirectionDataView.class);
        registerView(CooldownData.class, CooldownDataView.class);
        registerView(CriticalHitData.class, CriticalHitDataView.class);
        registerView(CustomNameVisibleData.class, CustomNameVisibleDataView.class);
        registerView(DamageableData.class, DamageableDataView.class);
        registerView(DamagingData.class, DamagingDataView.class);
        registerView(DecayableData.class, DecayableDataView.class);
        registerView(DelayableData.class, DelayableDataView.class);
        registerView(DespawnDelayData.class, DespawnDelayDataView.class);
        registerView(DirectionalData.class, DirectionalDataView.class);
        registerView(DisarmedData.class, DisarmedDataView.class);
        registerView(DisplayNameData.class, DisplayNameDataView.class);
        registerView(DropData.class, DropDataView.class);
        registerView(DurabilityData.class, DurabilityDataView.class);
        //registerView(EndGatewayData.class, EndGatewayDataView.class);
        registerView(ExperienceHolderData.class, ExperienceHolderDataView.class);
        registerView(ExpirableData.class, ExpirableDataView.class);
        registerView(ExplosionRadiusData.class, ExplosionRadiusDataView.class);
        registerView(ExpOrbData.class, ExpOrbDataView.class);
        registerView(ExtendedData.class, ExtendedDataView.class);
        registerView(FallDistanceData.class, FallDistanceDataView.class);
        registerView(FallingBlockData.class, FallingBlockDataView.class);
        registerView(FilledData.class, FilledDataView.class);
        registerView(FireworkRocketData.class, FireworkRocketDataView.class);
        registerView(FlammableData.class, FlammableDataView.class);
        registerView(FluidItemData.class, FluidItemDataView.class);
        registerView(FluidLevelData.class, FluidLevelDataView.class);
        registerView(FlyingAbilityData.class, FlyingAbilityDataView.class);
        registerView(FlyingData.class, FlyingDataView.class);
        registerView(FoodData.class, FoodDataView.class);
        registerView(FurnaceData.class, FurnaceDataView.class);
        registerView(FuseData.class, FuseDataView.class);
        registerView(GenerationData.class, GenerationDataView.class);
        registerView(GlowingData.class, GlowingDataView.class);
        registerView(GriefingData.class, GriefingDataView.class);
        registerView(GrowthData.class, GrowthDataView.class);
        registerView(HealthData.class, HealthDataView.class);
        registerView(HideData.class, HideDataView.class);
        registerView(HorseData.class, HorseDataView.class);
        registerView(IgniteableData.class, IgniteableDataView.class);
        registerView(InventoryItemData.class, InventoryItemDataView.class);
        registerView(InvisibilityData.class, InvisibilityDataView.class);
        registerView(InvulnerabilityData.class, InvulnerabilityDataView.class);
        registerView(InWallData.class, InWallDataView.class);
        registerView(JoinData.class, JoinDataView.class);
        registerView(KnockbackData.class, KnockbackDataView.class);
        registerView(LayeredData.class, LayeredDataView.class);
        registerView(LeashData.class, LeashDataView.class);
        registerView(LockableData.class, LockableDataView.class);
        registerView(MinecartBlockData.class, MinecartBlockDataView.class);
        registerView(MobSpawnerData.class, MobSpawnerDataView.class);
        registerView(MoistureData.class, MoistureDataView.class);
        registerView(NoteData.class, NoteDataView.class);
        registerView(OccupiedData.class, OccupiedDataView.class);
        registerView(OpenData.class, OpenDataView.class);
        registerView(PersistingData.class, PersistingDataView.class);
        registerView(PickupDelayData.class, PickupDelayDataView.class);
        registerView(PigSaddleData.class, PigSaddleDataView.class);
        registerView(PlaceableData.class, PlaceableDataView.class);
        registerView(PlayerCreatedData.class, PlayerCreatedDataView.class);
        registerView(PlayingData.class, PlayingDataView.class);
        registerView(PoweredData.class, PoweredDataView.class);
        registerView(RedstonePoweredData.class, RedstonePoweredDataView.class);
        registerView(RepresentedItemData.class, RepresentedItemDataView.class);
        registerView(RepresentedPlayerData.class, RepresentedPlayerDataView.class);
        registerView(ScreamingData.class, ScreamingDataView.class);
        registerView(SeamlessData.class, SeamlessDataView.class);
        registerView(ShatteringData.class, ShatteringDataView.class);
        registerView(ShearedData.class, ShearedDataView.class);
        registerView(SilentData.class, SilentDataView.class);
        registerView(SittingData.class, SittingDataView.class);
        registerView(SkinData.class, SkinDataView.class);
        registerView(SleepingData.class, SleepingDataView.class);
        registerView(SlimeData.class, SlimeDataView.class);
        registerView(SneakingData.class, SneakingDataView.class);
        registerView(SnowedData.class, SnowedDataView.class);
        registerView(SprintData.class, SprintDataView.class);
        registerView(StatisticData.class, StatisticDataView.class);
        //registerView(StructureData.class, StructureDataView.class);
        registerView(StuckArrowsData.class, StuckArrowsDataView.class);
        registerView(TameableData.class, TameableDataView.class);
        registerView(TargetedLocationData.class, TargetedLocationDataView.class);
        registerView(VehicleData.class, VehicleDataView.class);
        registerView(WetData.class, WetDataView.class);
        registerView(WireAttachmentData.class, WireAttachmentDataView.class);

        // Economy
        registerView(Account.class, AccountView.class);
        registerView(Currency.class, CurrencyView.class);

        // Entity
        registerView(Career.class, CareerView.class);
        registerView(EntityArchetype.class, EntityArchetypeView.class);
        registerView(EntitySnapshot.class, EntitySnapshotView.class);
        registerView(TradeOffer.class, TradeOfferView.class);

        // Event
        registerView(DamageSource.class, DamageSourceView.class);
        registerView(Event.class, EventView.class);

        // Fluid
        registerView(FluidStackSnapshot.class, FluidStackSnapshotView.class);
        registerView(FluidStack.class, FluidStackView.class);

        // Item
        registerView(FireworkEffect.class, FireworkEffectView.class);
        registerView(ItemEnchantment.class, ItemEnchantmentView.class);
        registerView(ItemStackSnapshot.class, ItemStackSnapshotView.class);
        registerView(ItemStack.class, ItemStackView.class);
        registerView(PotionEffect.class, PotionEffectView.class);

        // Misc.
        registerView(Color.class, ColorView.class);
        registerView(CommandSource.class, CommandSourceView.class);
        registerView(DyeColor.class, DyeColorView.class);
        registerView(Explosion.class, ExplosionView.class);
        registerView(Instant.class, InstantView.class);
        registerView(LocalDate.class, LocalDateView.class);
        registerView(RandomObjectTable.class, RandomObjectTableView.class);
        registerView(Statistic.class, StatisticView.class);
        registerView(Text.class, TextView.class);
        registerView(Vector3d.class, Vector3dView.class);
        registerView(Vector3i.class, Vector3iView.class);

        // Permission
        registerView(SubjectCollection.class, SubjectCollectionView.class);
        registerView(Subject.class, SubjectView.class);

        // Player
        registerView(Achievement.class, AchievementView.class);
        registerView(Ban.class, BanView.class);
        registerView(GameMode.class, GameModeView.class);
        registerView(GameProfile.class, GameProfileView.class);
        registerView(RespawnLocation.class, RespawnLocationView.class);

        // Tile-Entity
        registerView(PatternLayer.class, PatternLayerView.class);

        // Data
        supportedData = new ConcurrentHashMap<>();

        //supportedData.put("absorption", AbsorptionData.class);
        supportedData.put("achievements", AchievementData.class);
        supportedData.put("age", AgeableData.class);
        supportedData.put("aiEnabled", AgentData.class);
        supportedData.put("aggressive", AggressiveData.class);
        supportedData.put("angerLevel", AngerableData.class);
        supportedData.put("armorStand", ArmorStandData.class);
        supportedData.put("attached", AttachedData.class);
        supportedData.put("art", ArtData.class);                                    // variant
        supportedData.put("axis", AxisData.class);                                  // variant
        supportedData.put("author", AuthorData.class);
        supportedData.put("banner", BannerData.class);
        supportedData.put("beacon", BeaconData.class);
        supportedData.put("bigMushroom", BigMushroomData.class);                    // variant
        supportedData.put("block", BlockItemData.class);
        supportedData.put("breathing", BreathingData.class);
        supportedData.put("breedable", BreedableData.class);
        supportedData.put("brewingStand", BrewingStandData.class);
        supportedData.put("brick", BrickData.class);                                // variant
        supportedData.put("career", CareerData.class);                              // variant
        supportedData.put("charged", ChargedData.class);
        supportedData.put("coal", CoalData.class);                                  // variant
        supportedData.put("color", ColoredData.class);
        supportedData.put("command", CommandData.class);
        supportedData.put("comparator", ComparatorData.class);                      // variant
        supportedData.put("connectedDirection", ConnectedDirectionData.class);
        supportedData.put("cookedFish", CookedFishData.class);                      // variant
        supportedData.put("cooldown", CooldownData.class);
        supportedData.put("criticalHit", CriticalHitData.class);
        supportedData.put("customName", CustomNameVisibleData.class);
        supportedData.put("damageable", DamageableData.class);
        supportedData.put("damage", DamagingData.class);
        supportedData.put("decayable", DecayableData.class);
        supportedData.put("delay", DelayableData.class);
        supportedData.put("despawnDelay", DespawnDelayData.class);
        supportedData.put("direction", DirectionalData.class);
        supportedData.put("dirt", DirtData.class);                                  // variant
        supportedData.put("disarmed", DisarmedData.class);
        supportedData.put("displayName", DisplayNameData.class);
        supportedData.put("disguisedBlock", DisguisedBlockData.class);              // variant
        supportedData.put("dominantHand", DominantHandData.class);                  // variant
        supportedData.put("doublePlant", DoublePlantData.class);                    // variant
        supportedData.put("drops", DropData.class);
        supportedData.put("durability", DurabilityData.class);
        supportedData.put("dye", DyeableData.class);                                // variant
        //supportedData.put("endGateway", EndGatewayData.class);
        supportedData.put("enchantments", EnchantmentData.class);                   // list
        supportedData.put("experience", ExperienceHolderData.class);
        supportedData.put("expireTicks", ExpirableData.class);
        supportedData.put("explosionRadius", ExplosionRadiusData.class);
        supportedData.put("expOrb", ExpOrbData.class);
        supportedData.put("extended", ExtendedData.class);
        supportedData.put("fallDistance", FallDistanceData.class);
        supportedData.put("fallingBlock", FallingBlockData.class);
        supportedData.put("filled", FilledData.class);
        supportedData.put("fireworkEffects", FireworkEffectData.class);             // list
        supportedData.put("fireworkRocket", FireworkRocketData.class);
        supportedData.put("fish", FishData.class);                                  // variant
        supportedData.put("flammable", FlammableData.class);
        supportedData.put("fluid", FluidItemData.class);
        supportedData.put("fluidLevel", FluidLevelData.class);
        supportedData.put("fluidTanks", FluidTankData.class);                       // map
        supportedData.put("flyingAbility", FlyingAbilityData.class);
        supportedData.put("flying", FlyingData.class);
        supportedData.put("food", FoodData.class);
        supportedData.put("furnace", FurnaceData.class);
        supportedData.put("fuse", FuseData.class);
        supportedData.put("gameMode", GameModeData.class);                          // variant
        supportedData.put("generation", GenerationData.class);
        supportedData.put("glowing", GlowingData.class);
        supportedData.put("goldenApple", GoldenAppleData.class);                    // variant
        supportedData.put("griefs", GriefingData.class);
        supportedData.put("growth", GrowthData.class);
        supportedData.put("health", HealthData.class);
        supportedData.put("hide", HideData.class);
        supportedData.put("hinge", HingeData.class);                                // variant
        supportedData.put("horse", HorseData.class);
        supportedData.put("igniteable", IgniteableData.class);
        supportedData.put("inventory", InventoryItemData.class);
        supportedData.put("invisibility", InvisibilityData.class);
        supportedData.put("invulnerability", InvulnerabilityData.class);
        supportedData.put("inWall", InWallData.class);
        supportedData.put("joined", JoinData.class);
        supportedData.put("knockback", KnockbackData.class);
        supportedData.put("layer", LayeredData.class);
        supportedData.put("leash", LeashData.class);
        supportedData.put("lockToken", LockableData.class);
        supportedData.put("logAxis", LogAxisData.class);                            // variant
        supportedData.put("lore", LoreData.class);                                  // list
        supportedData.put("minecartBlock", MinecartBlockData.class);
        supportedData.put("mobSpawner", MobSpawnerData.class);
        supportedData.put("moisture", MoistureData.class);
        supportedData.put("note", NoteData.class);
        supportedData.put("ocelot", OcelotData.class);                              // variant
        supportedData.put("occupied", OccupiedData.class);
        supportedData.put("open", OpenData.class);
        supportedData.put("pages", PagedData.class);                                // list
        supportedData.put("passengers", PassengerData.class);                       // list
        supportedData.put("persists", PersistingData.class);
        supportedData.put("pickupDelay", PickupDelayData.class);
        supportedData.put("pickupRule", PickupRuleData.class);                      // variant
        supportedData.put("pigSaddle", PigSaddleData.class);
        supportedData.put("piston", PistonData.class);                              // variant
        supportedData.put("placeableOn", PlaceableData.class);
        supportedData.put("plant", PlantData.class);                                // variant
        supportedData.put("playerCreated", PlayerCreatedData.class);
        supportedData.put("playing", PlayingData.class);
        supportedData.put("portion", PortionData.class);                            // variant
        supportedData.put("potionEffects", PotionEffectData.class);                 // list
        supportedData.put("powered", PoweredData.class);
        supportedData.put("prismarine", PrismarineData.class);                      // variant
        supportedData.put("quartz", QuartzData.class);                              // variant
        supportedData.put("rabbit", RabbitData.class);                              // variant
        supportedData.put("railDirection", RailDirectionData.class);                // variant
        supportedData.put("redstonePower", RedstonePoweredData.class);
        supportedData.put("representedItem", RepresentedItemData.class);
        supportedData.put("representedPlayer", RepresentedPlayerData.class);
        supportedData.put("respawnLocations", RespawnLocationData.class);           // map
        supportedData.put("sand", SandData.class);                                  // variant
        supportedData.put("sandStone", SandstoneData.class);                        // variant
        supportedData.put("screaming", ScreamingData.class);
        supportedData.put("seamless", SeamlessData.class);
        supportedData.put("shatters", ShatteringData.class);
        supportedData.put("sheared", ShearedData.class);
        supportedData.put("shrub", ShrubData.class);                                // variant
        supportedData.put("sign", SignData.class);                                  // list
        supportedData.put("silent", SilentData.class);
        supportedData.put("sitting", SittingData.class);
        supportedData.put("skin", SkinData.class);
        supportedData.put("skull", SkullData.class);                                // variant
        supportedData.put("slab", SlabData.class);                                  // variant
        supportedData.put("sleeping", SleepingData.class);
        supportedData.put("slime", SlimeData.class);
        supportedData.put("sneaking", SneakingData.class);
        supportedData.put("snow", SnowedData.class);
        supportedData.put("spawn", SpawnableData.class);                            // variant
        supportedData.put("sprinting", SprintData.class);
        supportedData.put("stairShape", StairShapeData.class);                      // variant
        supportedData.put("statistics", StatisticData.class);                       // map
        supportedData.put("stone", StoneData.class);                                // variant
        supportedData.put("storedEnchantments", StoredEnchantmentData.class);       // list
        //supportedData.put("structure", StructureData.class);
        supportedData.put("stuckArrows", StuckArrowsData.class);
        supportedData.put("tamed", TameableData.class);
        supportedData.put("target", TargetedLocationData.class);
        supportedData.put("trades", TradeOfferData.class);                          // list
        supportedData.put("tree", TreeData.class);                                  // variant
        supportedData.put("vehicle", VehicleData.class);
        supportedData.put("wall", WallData.class);                                  // variant
        supportedData.put("wet", WetData.class);
        supportedData.put("wires", WireAttachmentData.class);


        // Properties
        supportedProperties = new ConcurrentHashMap<>();

        supportedProperties.put(AcceptsItems.class, "acceptsItems");
        supportedProperties.put(ApplicableEffectProperty.class, "applicableEffect");
        supportedProperties.put(ArmorSlotType.class, "armorSlotType");
        supportedProperties.put(ArmorTypeProperty.class, "armorType");
        supportedProperties.put(BlastResistanceProperty.class, "blastResistance");
        supportedProperties.put(BurningFuelProperty.class, "burningFuel");
        supportedProperties.put(DamageAbsorptionProperty.class, "damageAbsorption");
        supportedProperties.put(DominantHandProperty.class, "dominantHand");
        supportedProperties.put(EfficiencyProperty.class, "efficiency");
        supportedProperties.put(EquipmentProperty.class, "equipmentType");
        supportedProperties.put(EquipmentSlotType.class, "equiptmentSlotType");
        supportedProperties.put(EyeHeightProperty.class, "eyeHeight");
        supportedProperties.put(EyeLocationProperty.class, "eyeLocation");
        supportedProperties.put(FlammableProperty.class, "flammable");
        supportedProperties.put(FluidTemperatureProperty.class, "fluidTemperature");
        supportedProperties.put(FluidViscosityProperty.class, "fluidViscosity");
        supportedProperties.put(FoodRestorationProperty.class, "foodRestoration");
        supportedProperties.put(FullBlockSelectionBoxProperty.class, "fullBlockSelectionBox");
        supportedProperties.put(GravityAffectedProperty.class, "gravityAffected");
        supportedProperties.put(GroundLuminanceProperty.class, "groundLuminance");
        supportedProperties.put(HardnessProperty.class, "hardness");
        supportedProperties.put(HeldItemProperty.class, "heldItem");
        supportedProperties.put(Identifiable.class, "identifiable");
        supportedProperties.put(IndirectlyPoweredProperty.class, "indirectlyPowered");
        supportedProperties.put(InventoryCapacity.class, "inventoryCapacity");
        supportedProperties.put(InventoryDimension.class, "inventoryDimension");
        supportedProperties.put(InventoryTitle.class, "inventoryTitle");
        supportedProperties.put(LightEmissionProperty.class, "lightEmission");
        supportedProperties.put(MatterProperty.class, "matter");
        supportedProperties.put(PassableProperty.class, "passable");
        supportedProperties.put(PoweredProperty.class, "powered");
        supportedProperties.put(ReplaceableProperty.class, "replaceable");
        supportedProperties.put(SaturationProperty.class, "saturationProperty");
        supportedProperties.put(SkyLuminanceProperty.class, "skyLuminance");
        supportedProperties.put(SlotIndex.class, "slotIndex");
        supportedProperties.put(SlotPos.class, "slotPos");
        supportedProperties.put(SlotSide.class, "slotSide");
        supportedProperties.put(SmeltableProperty.class, "smeltable");
        supportedProperties.put(SolidCubeProperty.class, "solidCube");
        supportedProperties.put(StatisticsTrackedProperty.class, "statisticsTracked");
        supportedProperties.put(SurrogateBlockProperty.class, "surrogateBlock");
        supportedProperties.put(TemperatureProperty.class, "temperature");
        supportedProperties.put(ToolTypeProperty.class, "toolType");
        supportedProperties.put(UnbreakableProperty.class, "unbreakable");
        supportedProperties.put(UseLimitProperty.class, "useLimit");

        logger.info("Done loading serializers");
    }

    private void _register(Class handledClass, Class cacheClass) {
        serializers.put(handledClass, new BaseSerializer<>(handledClass, cacheClass));
    }

    /**
     * Registers an object as a cached object for the Web-API.
     * @param handledClass The class of the live object which is turned into a cached object.
     * @param cacheClass The class of the cached object.
     * @param <T> The type of the live object.
     */
    public <T> void registerCache(Class<? extends T> handledClass, Class<? extends CachedObject<T>> cacheClass) {
        _register(handledClass, cacheClass);
    }

    /**
     * Registers a view which helps turn an object into json.
     * @param handledClass The class of the live object which is turned into json using the view class.
     * @param viewClass The class of the view which helps turn the object into json.
     * @param <T> The type of the live object.
     */
    public <T> void registerView(Class<? extends T> handledClass, Class<? extends BaseView<T>> viewClass) {
        _register(handledClass, viewClass);
    }

    /**
     * Gets all DataHolder types that are supported by the Web-API
     * @return A map from json key to DataHolder type
     */
    public Map<String, Class<? extends DataManipulator<?, ?>>> getSupportedData() {
        return supportedData;
    }

    /**
     * Gets all PropertyHolder types that are supported by the Web-API
     * @return A map from json key to PropertyHolder type
     */
    public Map<Class<? extends Property<?, ?>>, String> getSupportedProperties() {
        return supportedProperties;
    }

    private Type[] getGenericTypes(Class baseClass, Class targetClass) {
        Queue<Class> queue = new LinkedList<>();
        queue.add(baseClass);
        while (!queue.isEmpty()) {
            Class c = queue.poll();
            Type parent = c.getGenericSuperclass();
            if (parent instanceof ParameterizedType && ((ParameterizedType)parent).getRawType().equals(targetClass)) {
                return ((ParameterizedType)parent).getActualTypeArguments();
            } else if (parent instanceof Class) {
                queue.add((Class) parent);
            }
            for (Type iFace : c.getGenericInterfaces()) {
                if (iFace instanceof ParameterizedType && ((ParameterizedType)iFace).getRawType().equals(targetClass)) {
                    return ((ParameterizedType)iFace).getActualTypeArguments();
                } else if (iFace instanceof Class) {
                    queue.add((Class) iFace);
                }
            }
        }
        return new Type[0];
    }

    /**
     * Gets the View class used to represent a certain class, if present
     * @param clazz The class which should be check for an available view
     * @return The view class that should be used for serialization instead of the original class, if available.
     */
    public Optional<Type> getViewFor(Class clazz) {
        BaseSerializer ser = serializers.get(clazz);
        if (ser != null) {
            return Optional.of(ser.getCacheClass());
        }

        // Check if we have a variant, list or mapped data type
        if (VariantData.class.isAssignableFrom(clazz)) {
            Type[] ts = getGenericTypes(clazz, VariantData.class);
            return Optional.of(ts[0]);
        } else if (ListData.class.isAssignableFrom(clazz)) {
            Type elemType = getGenericTypes(clazz, ListData.class)[0];
            return Optional.of(new ParameterizedType() {
                @Override
                public Type[] getActualTypeArguments() {
                    return new Type[]{ elemType };
                }

                @Override
                public Type getRawType() {
                    return List.class;
                }

                @Override
                public Type getOwnerType() {
                    return null;
                }
            });
        } else if (MappedData.class.isAssignableFrom(clazz)) {
            Type[] ts = getGenericTypes(clazz, MappedData.class);
            return Optional.of(new ParameterizedType() {
                @Override
                public Type[] getActualTypeArguments() {
                    return new Type[]{ ts[0], ts[1] };
                }

                @Override
                public Type getRawType() {
                    return Map.class;
                }

                @Override
                public Type getOwnerType() {
                    return null;
                }
            });
        } else if (AbstractProperty.class.isAssignableFrom(clazz)) {
            Type[] ts = getGenericTypes(clazz, AbstractProperty.class);
            return Optional.of(ts[1]);
        }

        // Try and find a parent class serializer (e.g. CatalogType) which matches closest
        Optional<BaseSerializer> optSer = serializers.values().stream()
                .filter(s -> s.getHandledClass().isAssignableFrom(clazz))
                .sorted((s1, s2) -> {
                    // Sort possibly multiple serializers by parent-child relation, if available
                    if (s1.getHandledClass().isAssignableFrom(s2.getHandledClass()))
                        return 1;
                    else if (s2.getHandledClass().isAssignableFrom(s1.getHandledClass()))
                        return -1;
                    return 0;
                })
                .findFirst();

        return optSer.map(BaseSerializer::getHandledClass);
    }

    public ObjectMapper getDefaultObjectMapper(boolean xml, boolean details, TreeNode perms) {
        if (perms == null) {
            throw new NullPointerException("Permissions may not be null");
        }

        ObjectMapper om = xml ? new XmlMapper() : new ObjectMapper();
        if (xml) {
            ((XmlMapper)om).configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        }
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule mod = new SimpleModule();
        for (Map.Entry<Class, BaseSerializer> entry : serializers.entrySet()) {
            mod.addSerializer(entry.getKey(), entry.getValue());
        }
        mod.addDeserializer(ItemStack.class, new ItemStackDeserializer());
        mod.addDeserializer(BlockState.class, new BlockStateDeserializer());
        mod.addDeserializer(ItemStackSnapshot.class, new ItemStackSnapshotDeserializer());
        mod.addDeserializer(CachedLocation.class, new CachedLocationDeserializer());
        mod.addDeserializer(CachedPlayer.class, new CachedPlayerDeserializer());
        mod.addDeserializer(CachedWorld.class, new CachedWorldDeserializer());
        mod.addDeserializer(CachedCatalogType.class, new CachedCatalogTypeDeserializer<>(CatalogType.class));
        om.registerModule(mod);

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(BaseFilter.ID, new BaseFilter(details, perms));
        om.setFilterProvider(filterProvider);

        om.setAnnotationIntrospector(new AnnotationIntrospector());

        return om;
    }
}
