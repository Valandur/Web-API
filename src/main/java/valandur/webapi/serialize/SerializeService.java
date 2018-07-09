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
import org.spongepowered.api.advancement.Advancement;
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
import org.spongepowered.api.item.enchantment.Enchantment;
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
import org.spongepowered.plugin.meta.PluginDependency;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.block.CachedBlockSnapshot;
import valandur.webapi.cache.block.CachedBlockState;
import valandur.webapi.cache.command.CachedCommand;
import valandur.webapi.cache.data.*;
import valandur.webapi.cache.economy.CachedAccount;
import valandur.webapi.cache.economy.CachedCurrency;
import valandur.webapi.cache.entity.*;
import valandur.webapi.cache.event.CachedDamageSource;
import valandur.webapi.cache.event.CachedEvent;
import valandur.webapi.cache.fluid.CachedFluidStack;
import valandur.webapi.cache.fluid.CachedFluidStackSnapshot;
import valandur.webapi.cache.item.*;
import valandur.webapi.cache.misc.*;
import valandur.webapi.cache.permission.CachedSubject;
import valandur.webapi.cache.permission.CachedSubjectCollection;
import valandur.webapi.cache.player.*;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.cache.plugin.CachedPluginDependency;
import valandur.webapi.cache.tileentity.CachedPatternLayer;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.*;
import valandur.webapi.serialize.deserialize.*;
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
        registerCache(Advancement.class, CachedAdvancement.class);
        _register(CatalogType.class, CachedCatalogType.class);
        registerCache(Cause.class, CachedCause.class);
        registerCache(Chunk.class, CachedChunk.class);
        registerCache(CommandMapping.class, CachedCommand.class);
        registerCache(Entity.class, CachedEntity.class);
        registerCache(Inventory.class, CachedInventory.class);
        registerCache(Location.class, CachedLocation.class);
        registerCache(Player.class, CachedPlayer.class);
        registerCache(PluginContainer.class, CachedPluginContainer.class);
        registerCache(PluginDependency.class, CachedPluginDependency.class);
        registerCache(TileEntity.class, CachedTileEntity.class);
        registerCache(Transform.class, CachedTransform.class);
        registerCache(World.class, CachedWorld.class);
        registerCache(WorldBorder.class, CachedWorldBorder.class);

        // Block
        registerCache(BlockSnapshot.class, CachedBlockSnapshot.class);
        registerCache(BlockState.class, CachedBlockState.class);

        // Data
        registerCache(ListData.class, CachedListData.class);
        registerCache(MappedData.class, CachedMappedData.class);
        registerCache(VariantData.class, CachedVariantData.class);

        registerCache(AbsorptionData.class, CachedAbsorptionData.class);
        //registerCache(AchievementData.class, AchievementDataView.class);
        registerCache(AgeableData.class, CachedAgeableData.class);
        registerCache(AgentData.class, CachedAgentData.class);
        registerCache(AggressiveData.class, CachedAggressiveData.class);
        registerCache(AngerableData.class, CachedAngerableData.class);
        registerCache(ArmorStandData.class, CachedArmorStandData.class);
        registerCache(AttachedData.class, CachedAttachedData.class);
        registerCache(AuthorData.class, CachedAuthorData.class);
        registerCache(BannerData.class, CachedBannerData.class);
        registerCache(BeaconData.class, CachedBeaconData.class);
        registerCache(BlockItemData.class, CachedBlockItemData.class);
        registerCache(BreathingData.class, CachedBreathingData.class);
        registerCache(BreedableData.class, CachedBreedableData.class);
        registerCache(BrewingStandData.class, CachedBrewingStandData.class);
        registerCache(ChargedData.class, CachedChargedData.class);
        registerCache(ColoredData.class, CachedColoredData.class);
        registerCache(CommandData.class, CachedCommandData.class);
        registerCache(ConnectedDirectionData.class, CachedConnectedDirectionData.class);
        registerCache(CooldownData.class, CachedCooldownData.class);
        registerCache(CriticalHitData.class, CachedCriticalHitData.class);
        registerCache(CustomNameVisibleData.class, CachedCustomNameVisibleData.class);
        registerCache(DamageableData.class, CachedDamageableData.class);
        registerCache(DamagingData.class, CachedDamagingData.class);
        registerCache(DecayableData.class, CachedDecayableData.class);
        registerCache(DelayableData.class, CachedDelayableData.class);
        registerCache(DespawnDelayData.class, CachedDespawnDelayData.class);
        registerCache(DirectionalData.class, CachedDirectionalData.class);
        registerCache(DisarmedData.class, CachedDisarmedData.class);
        registerCache(DisplayNameData.class, CachedDisplayNameData.class);
        registerCache(DropData.class, CachedDropData.class);
        registerCache(DurabilityData.class, CachedDurabilityData.class);
        registerCache(EndGatewayData.class, CachedEndGatewayData.class);
        registerCache(ExperienceHolderData.class, CachedExperienceHolderData.class);
        registerCache(ExpirableData.class, CachedExpirableData.class);
        registerCache(ExplosionRadiusData.class, CachedExplosionRadiusData.class);
        registerCache(ExpOrbData.class, CachedExpOrbData.class);
        registerCache(ExtendedData.class, CachedExtendedData.class);
        registerCache(FallDistanceData.class, CachedFallDistanceData.class);
        registerCache(FallingBlockData.class, CachedFallingBlockData.class);
        registerCache(FilledData.class, CachedFilledData.class);
        registerCache(FireworkRocketData.class, CachedFireworkRocketData.class);
        registerCache(FlammableData.class, CachedFlammableData.class);
        registerCache(FluidItemData.class, CachedFluidItemData.class);
        registerCache(FluidLevelData.class, CachedFluidLevelData.class);
        registerCache(FlyingAbilityData.class, CachedFlyingAbilityData.class);
        registerCache(FlyingData.class, CachedFlyingData.class);
        registerCache(FoodData.class, CachedFoodData.class);
        registerCache(FurnaceData.class, CachedFurnaceData.class);
        registerCache(FuseData.class, CachedFuseData.class);
        registerCache(GenerationData.class, CachedGenerationData.class);
        registerCache(GlowingData.class, CachedGlowingData.class);
        registerCache(GriefingData.class, CachedGriefingData.class);
        registerCache(GrowthData.class, CachedGrowthData.class);
        registerCache(HealthData.class, CachedHealthData.class);
        registerCache(HideData.class, CachedHideData.class);
        registerCache(HorseData.class, CachedHorseData.class);
        registerCache(IgniteableData.class, CachedIgniteableData.class);
        registerCache(InventoryItemData.class, CachedInventoryItemData.class);
        registerCache(InvisibilityData.class, CachedInvisibilityData.class);
        registerCache(InvulnerabilityData.class, CachedInvulnerabilityData.class);
        registerCache(InWallData.class, CachedInWallData.class);
        registerCache(JoinData.class, CachedJoinData.class);
        registerCache(KnockbackData.class, CachedKnockbackData.class);
        registerCache(LayeredData.class, CachedLayeredData.class);
        registerCache(LeashData.class, CachedLeashData.class);
        registerCache(LockableData.class, CachedLockableData.class);
        registerCache(MinecartBlockData.class, CachedMinecartBlockData.class);
        registerCache(MobSpawnerData.class, CachedMobSpawnerData.class);
        registerCache(MoistureData.class, CachedMoistureData.class);
        registerCache(NoteData.class, CachedNoteData.class);
        registerCache(OccupiedData.class, CachedOccupiedData.class);
        registerCache(OpenData.class, CachedOpenData.class);
        registerCache(PersistingData.class, CachedPersistingData.class);
        registerCache(PickupDelayData.class, CachedPickupDelayData.class);
        registerCache(PigSaddleData.class, CachedPigSaddleData.class);
        registerCache(PlaceableData.class, CachedPlaceableData.class);
        registerCache(PlayerCreatedData.class, CachedPlayerCreatedData.class);
        registerCache(PlayingData.class, CachedPlayingData.class);
        registerCache(PoweredData.class, CachedPoweredData.class);
        registerCache(RedstonePoweredData.class, CachedRedstonePoweredData.class);
        registerCache(RepresentedItemData.class, CachedRepresentedItemData.class);
        registerCache(RepresentedPlayerData.class, CachedRepresentedPlayerData.class);
        registerCache(ScreamingData.class, CachedScreamingData.class);
        registerCache(SeamlessData.class, CachedSeamlessData.class);
        registerCache(ShatteringData.class, CachedShatteringData.class);
        registerCache(ShearedData.class, CachedShearedData.class);
        registerCache(SilentData.class, CachedSilentData.class);
        registerCache(SittingData.class, CachedSittingData.class);
        registerCache(SkinData.class, CachedSkinData.class);
        registerCache(SleepingData.class, CachedSleepingData.class);
        registerCache(SlimeData.class, CachedSlimeData.class);
        registerCache(SneakingData.class, CachedSneakingData.class);
        registerCache(SnowedData.class, CachedSnowedData.class);
        registerCache(SprintData.class, CachedSprintData.class);
        registerCache(StatisticData.class, CachedStatisticData.class);
        registerCache(StructureData.class, CachedStructureData.class);
        registerCache(StuckArrowsData.class, CachedStuckArrowsData.class);
        registerCache(TameableData.class, CachedTameableData.class);
        registerCache(TargetedLocationData.class, CachedTargetedLocationData.class);
        registerCache(VehicleData.class, CachedVehicleData.class);
        registerCache(WetData.class, CachedWetData.class);
        registerCache(WireAttachmentData.class, CachedWireAttachmentData.class);

        // Economy
        registerCache(Account.class, CachedAccount.class);
        registerCache(Currency.class, CachedCurrency.class);

        // Entity
        registerCache(Career.class, CachedCareer.class);
        registerCache(EntityArchetype.class, CachedEntityArchetype.class);
        registerCache(EntitySnapshot.class, CachedEntitySnapshot.class);
        registerCache(TradeOffer.class, CachedTradeOffer.class);

        // Event
        registerCache(DamageSource.class, CachedDamageSource.class);
        registerCache(Event.class, CachedEvent.class);

        // Fluid
        registerCache(FluidStackSnapshot.class, CachedFluidStackSnapshot.class);
        registerCache(FluidStack.class, CachedFluidStack.class);

        // Item
        registerCache(FireworkEffect.class, CachedFireworkEffect.class);
        registerCache(Enchantment.class, CachedEnchantment.class);
        registerCache(ItemStackSnapshot.class, CachedItemStackSnapshot.class);
        registerCache(ItemStack.class, CachedItemStack.class);
        registerCache(PotionEffect.class, CachedPotionEffect.class);

        // Misc.
        registerCache(Color.class, CachedColor.class);
        registerCache(CommandSource.class, CachedCommandSource.class);
        registerCache(DyeColor.class, CachedDyeColor.class);
        registerCache(Explosion.class, CachedExplosion.class);
        registerCache(Instant.class, CachedInstant.class);
        registerCache(LocalDate.class, CachedLocalDate.class);
        registerCache(RandomObjectTable.class, CachedRandomObjectTable.class);
        registerCache(Text.class, CachedText.class);
        registerCache(Vector3d.class, CachedVector3d.class);
        registerCache(Vector3i.class, CachedVector3i.class);

        // Permission
        registerCache(SubjectCollection.class, CachedSubjectCollection.class);
        registerCache(Subject.class, CachedSubject.class);

        // Player
        //registerCache(Achievement.class, AchievementView.class);
        registerCache(Ban.class, CachedBan.class);
        registerCache(GameProfile.class, CachedGameProfile.class);
        registerCache(RespawnLocation.class, CachedRespawnLocation.class);

        // Tile-Entity
        registerCache(PatternLayer.class, CachedPatternLayer.class);

        // Data
        supportedData = new ConcurrentHashMap<>();

        supportedData.put("absorption", AbsorptionData.class);
        //supportedData.put("achievements", AchievementData.class);
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
        supportedData.put("endGateway", EndGatewayData.class);
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
        supportedData.put("structure", StructureData.class);
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
        supportedProperties.put(GuiIdProperty.class, "guiId");
        supportedProperties.put(HardnessProperty.class, "hardness");
        supportedProperties.put(HeldItemProperty.class, "heldItem");
        supportedProperties.put(Identifiable.class, "identifiable");
        supportedProperties.put(IndirectlyPoweredProperty.class, "indirectlyPowered");
        supportedProperties.put(InstrumentProperty.class, "instrument");
        supportedProperties.put(InventoryCapacity.class, "inventoryCapacity");
        supportedProperties.put(InventoryDimension.class, "inventoryDimension");
        supportedProperties.put(InventoryTitle.class, "inventoryTitle");
        supportedProperties.put(LightEmissionProperty.class, "lightEmission");
        supportedProperties.put(MatterProperty.class, "matter");
        supportedProperties.put(PassableProperty.class, "passable");
        supportedProperties.put(PoweredProperty.class, "powered");
        supportedProperties.put(RecordProperty.class, "record");
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
