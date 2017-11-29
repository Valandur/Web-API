package valandur.webapi.serialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.mutable.*;
import org.spongepowered.api.data.manipulator.mutable.block.*;
import org.spongepowered.api.data.manipulator.mutable.entity.*;
import org.spongepowered.api.data.manipulator.mutable.item.*;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BannerData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BeaconData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.extra.fluid.FluidStack;
import org.spongepowered.api.extra.fluid.FluidStackSnapshot;
import org.spongepowered.api.extra.fluid.data.manipulator.mutable.FluidTankData;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.api.serialize.ISerializeService;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedCause;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.serialize.deserialize.BlockStateDeserializer;
import valandur.webapi.serialize.deserialize.ItemStackDeserializer;
import valandur.webapi.serialize.deserialize.ItemStackSnapshotDeserializer;
import valandur.webapi.serialize.deserialize.LocationDeserializer;
import valandur.webapi.serialize.view.block.BlockSnapshotView;
import valandur.webapi.serialize.view.block.BlockStateView;
import valandur.webapi.serialize.view.data.*;
import valandur.webapi.serialize.view.entity.CareerView;
import valandur.webapi.serialize.view.entity.TradeOfferView;
import valandur.webapi.serialize.view.event.DamageSourceView;
import valandur.webapi.serialize.view.event.EventView;
import valandur.webapi.serialize.view.fluid.FluidStackSnapshotView;
import valandur.webapi.serialize.view.fluid.FluidStackView;
import valandur.webapi.serialize.view.item.*;
import valandur.webapi.serialize.view.misc.*;
import valandur.webapi.serialize.view.player.*;
import valandur.webapi.serialize.view.tileentity.PatternLayerView;
import valandur.webapi.util.Util;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializeService implements ISerializeService {

    private Map<Class, BaseSerializer> serializers;
    private Map<String, Class<? extends DataManipulator>> supportedData;


    public void init() {
        Logger logger = WebAPI.getLogger();

        logger.info("Loading serializers...");

        serializers = new ConcurrentHashMap<>();

        // Cached Objects
        registerCache(Entity.class, CachedEntity.class);
        registerCache(Cause.class, CachedCause.class);
        registerCache(Inventory.class, CachedInventory.class);
        registerCache(CatalogType.class, CachedCatalogType.class);
        registerCache(Location.class, CachedLocation.class);
        registerCache(Player.class, CachedPlayer.class);
        registerCache(PluginContainer.class, CachedPluginContainer.class);
        registerCache(TileEntity.class, CachedTileEntity.class);
        registerCache(World.class, CachedWorld.class);

        // Block
        registerView(BlockSnapshot.class, BlockSnapshotView.class);
        registerView(BlockState.class, BlockStateView.class);

        // Data
        registerView(AchievementData.class, AchievementDataView.class);
        registerView(AgeableData.class, AgeableDataView.class);
        registerView(BannerData.class, BannerDataView.class);
        registerView(BeaconData.class, BeaconDataView.class);
        registerView(BreathingData.class, BreathingDataView.class);
        registerView(BreedableData.class, BreedableDataView.class);
        registerView(ConnectedDirectionData.class, ConnectedDirectionDataView.class);
        registerView(DurabilityData.class, DurabilityDataView.class);
        registerView(ExperienceHolderData.class, ExperienceHolderDataView.class);
        registerView(FoodData.class, FoodDataView.class);
        registerView(HealthData.class, HealthDataView.class);
        registerView(JoinData.class, JoinDataView.class);
        registerView(ListData.class, ListDataView.class);
        registerView(MappedData.class, MappedDataView.class);
        registerView(PoweredData.class, PoweredDataView.class);
        registerView(RedstonePoweredData.class, RedstonePoweredDataView.class);
        registerView(ShearedData.class, ShearedDataView.class);
        registerView(TameableData.class, TameableDataView.class);
        registerView(VariantData.class, VariantDataView.class);
        registerView(WetData.class, WetDataView.class);

        // Entity
        registerView(Career.class, CareerView.class);
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
        registerView(ItemType.class, ItemTypeView.class);
        registerView(PotionEffectType.class, PotionEffectTypeView.class);
        registerView(PotionEffect.class, PotionEffectView.class);

        // Misc.
        registerView(Color.class, ColorView.class);
        registerView(CommandSource.class, CommandSourceView.class);
        registerView(Direction.class, DirectionView.class);
        registerView(DyeColor.class, DyeColorView.class);
        registerView(Explosion.class, ExplosionView.class);
        registerView(Instant.class, InstantView.class);
        registerView(Statistic.class, StatisticView.class);
        registerView(Text.class, TextView.class);
        registerView(Vector3d.class, Vector3dView.class);
        registerView(Vector3i.class, Vector3iView.class);

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

        supportedData.put("achievements", AchievementData.class);
        supportedData.put("age", AgeableData.class);
        supportedData.put("art", ArtData.class);                                    // variant
        supportedData.put("axis", AxisData.class);                                  // variant
        supportedData.put("banner", BannerData.class);
        supportedData.put("beacon", BeaconData.class);
        supportedData.put("bigMushroom", BigMushroomData.class);                    // variant
        supportedData.put("breathing", BreathingData.class);
        supportedData.put("breedable", BreedableData.class);
        supportedData.put("brick", BrickData.class);                                // variant
        supportedData.put("career", CareerData.class);                              // variant
        supportedData.put("coal", CoalData.class);                                  // variant
        supportedData.put("comparator", ComparatorData.class);                      // variant
        supportedData.put("connectedDirection", ConnectedDirectionData.class);
        supportedData.put("cookedFish", CookedFishData.class);                      // variant
        supportedData.put("dirt", DirtData.class);                                  // variant
        supportedData.put("disguisedBlock", DisguisedBlockData.class);              // variant
        supportedData.put("dominantHand", DominantHandData.class);                  // variant
        supportedData.put("doublePlant", DoublePlantData.class);                    // variant
        supportedData.put("durability", DurabilityData.class);
        supportedData.put("dye", DyeableData.class);                                // variant
        supportedData.put("enchantments", EnchantmentData.class);                   // list
        supportedData.put("experience", ExperienceHolderData.class);
        supportedData.put("fireworkEffects", FireworkEffectData.class);             // list
        supportedData.put("fish", FishData.class);                                  // variant
        supportedData.put("fluidTank", FluidTankData.class);                        // map
        supportedData.put("food", FoodData.class);
        supportedData.put("gameMode", GameModeData.class);                          // variant
        supportedData.put("goldenApple", GoldenAppleData.class);                    // variant
        supportedData.put("health", HealthData.class);
        supportedData.put("hinge", HingeData.class);                                // variant
        supportedData.put("joined", JoinData.class);
        supportedData.put("logAxis", LogAxisData.class);                            // variant
        supportedData.put("lore", LoreData.class);                                  // list
        supportedData.put("ocelot", OcelotData.class);                              // variant
        supportedData.put("pages", PagedData.class);                                // list
        supportedData.put("passengers", PassengerData.class);                       // list
        supportedData.put("pickupRule", PickupRuleData.class);                      // variant
        supportedData.put("piston", PistonData.class);                              // variant
        supportedData.put("plant", PlantData.class);                                // variant
        supportedData.put("portion", PortionData.class);                            // variant
        supportedData.put("potionEffects", PotionEffectData.class);                 // list
        supportedData.put("powered", PoweredData.class);
        supportedData.put("prismarine", PrismarineData.class);                      // variant
        supportedData.put("quartz", QuartzData.class);                              // variant
        supportedData.put("rabbit", RabbitData.class);                              // variant
        supportedData.put("railDirection", RailDirectionData.class);                // variant
        supportedData.put("redstonePower", RedstonePoweredData.class);
        supportedData.put("respawnLocation", RespawnLocationData.class);            // map
        supportedData.put("sand", SandData.class);                                  // variant
        supportedData.put("sandStone", SandstoneData.class);                        // variant
        supportedData.put("sheared", ShearedData.class);
        supportedData.put("shrub", ShrubData.class);                                // variant
        supportedData.put("sign", SignData.class);                                  // list
        supportedData.put("skull", SkullData.class);                                // variant
        supportedData.put("slab", SlabData.class);                                  // variant
        supportedData.put("spawn", SpawnableData.class);                            // variant
        supportedData.put("stairShape", StairShapeData.class);                      // variant
        supportedData.put("statistics", StatisticData.class);                       // map
        supportedData.put("stone", StoneData.class);                                // variant
        supportedData.put("storedEnchantments", StoredEnchantmentData.class);       // list
        supportedData.put("tamed", TameableData.class);
        supportedData.put("trades", TradeOfferData.class);                          // list
        supportedData.put("tree", TreeData.class);                                  // variant
        supportedData.put("wall", WallData.class);                                  // variant
        supportedData.put("wet", WetData.class);

        logger.info("Done loading serializers");
    }

    @Override
    public <T> void registerCache(Class<? extends T> handledClass, Class<? extends ICachedObject<T>> cacheClass) {
        serializers.put(handledClass, new BaseSerializer<>(handledClass, cacheClass));
    }
    @Override
    public <T> void registerView(Class<? extends T> handledClass, Class<? extends BaseView<T>> viewClass) {
        serializers.put(handledClass, new BaseSerializer<>(handledClass, viewClass));
    }

    @Override
    public Map<String, Class<? extends DataManipulator>> getSupportedData() {
        return supportedData;
    }

    @Override
    public String toString(Object obj, boolean xml, boolean details, TreeNode<String, Boolean> perms) {
        ObjectMapper mapper = getDefaultObjectMapper(xml, details, perms);

        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    @Override
    public JsonNode serialize(Object obj, boolean xml, boolean details, TreeNode<String, Boolean> perms) {
        ObjectMapper mapper = getDefaultObjectMapper(xml, details, perms);
        return mapper.valueToTree(obj);
    }
    @Override
    public JsonNode deserialize(Reader reader, boolean xml, TreeNode<String, Boolean> perms) throws IOException {
        ObjectMapper mapper = getDefaultObjectMapper(xml, true, perms);
        return mapper.readTree(reader);
    }

    @Override
    public <T> T deserialize(String content, boolean xml, JavaType type, TreeNode<String, Boolean> perms) throws IOException {
        ObjectMapper mapper = getDefaultObjectMapper(xml, true, perms);
        return mapper.readValue(content, type);
    }
    @Override
    public <T> T deserialize(JsonNode content, Class<T> clazz, TreeNode<String, Boolean> perms) throws IOException {
        ObjectMapper mapper = getDefaultObjectMapper(false, true, perms);
        return mapper.treeToValue(content, clazz);
    }

    @Override
    public JsonNode classToJson(Class c) {
        ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.put("name", c.getName());
        json.put("parent", c.getSuperclass() != null ? c.getSuperclass().getName() : null);

        ObjectNode jsonFields = JsonNodeFactory.instance.objectNode();
        Field[] fs = Util.getAllFields(c);
        for (Field f : fs) {
            ObjectNode jsonField = JsonNodeFactory.instance.objectNode();

            f.setAccessible(true);

            jsonField.put("type", f.getType().getName());

            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            int mod = f.getModifiers();
            if (Modifier.isAbstract(mod)) arr.add("abstract");
            if (Modifier.isFinal(mod)) arr.add("final");
            if (Modifier.isInterface(mod)) arr.add("interface");
            if (Modifier.isNative(mod)) arr.add("native");
            if (Modifier.isPrivate(mod)) arr.add("private");
            if (Modifier.isProtected(mod)) arr.add("protected");
            if (Modifier.isPublic(mod)) arr.add("public");
            if (Modifier.isStatic(mod)) arr.add("static");
            if (Modifier.isStrict(mod)) arr.add("strict");
            if (Modifier.isSynchronized(mod)) arr.add("synchronized");
            if (Modifier.isTransient(mod)) arr.add("transient");
            if (Modifier.isVolatile(mod)) arr.add("volatile");
            jsonField.set("modifiers", arr);

            if (f.getDeclaringClass() != c) {
                jsonField.put("from", f.getDeclaringClass().getName());
            }

            jsonFields.set(f.getName(), jsonField);
        }
        json.set("fields", jsonFields);

        ObjectNode jsonMethods = JsonNodeFactory.instance.objectNode();
        Method[] ms = Util.getAllMethods(c);
        for (Method m : ms) {
            ObjectNode jsonMethod = JsonNodeFactory.instance.objectNode();

            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            int mod = m.getModifiers();
            if (Modifier.isAbstract(mod)) arr.add("abstract");
            if (Modifier.isFinal(mod)) arr.add("final");
            if (Modifier.isInterface(mod)) arr.add("interface");
            if (Modifier.isNative(mod)) arr.add("native");
            if (Modifier.isPrivate(mod)) arr.add("private");
            if (Modifier.isProtected(mod)) arr.add("protected");
            if (Modifier.isPublic(mod)) arr.add("public");
            if (Modifier.isStatic(mod)) arr.add("static");
            if (Modifier.isStrict(mod)) arr.add("strict");
            if (Modifier.isSynchronized(mod)) arr.add("synchronized");
            if (Modifier.isTransient(mod)) arr.add("transient");
            if (Modifier.isVolatile(mod)) arr.add("volatile");
            jsonMethod.set("modifiers", arr);

            ArrayNode arr2 = JsonNodeFactory.instance.arrayNode();
            for (Parameter p : m.getParameters()) {
                arr2.add(p.getType().getName());
            }
            jsonMethod.set("params", arr2);

            jsonMethod.put("return", m.getReturnType().getName());

            if (m.getDeclaringClass() != c) {
                jsonMethod.put("from", m.getDeclaringClass().getName());
            }

            jsonMethods.set(m.getName(), jsonMethod);
        }
        json.set("methods", jsonMethods);

        return json;
    }

    private ObjectMapper getDefaultObjectMapper(boolean xml, boolean details, TreeNode<String, Boolean> perms) {
        if (perms == null) {
            throw new NullPointerException("Permissions may not be null");
        }

        ObjectMapper om = xml ? new XmlMapper() : new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule mod = new SimpleModule();
        for (Map.Entry<Class, BaseSerializer> entry : serializers.entrySet()) {
            mod.addSerializer(entry.getKey(), entry.getValue());
        }
        mod.addDeserializer(ItemStack.class, new ItemStackDeserializer());
        mod.addDeserializer(BlockState.class, new BlockStateDeserializer());
        mod.addDeserializer(ItemStackSnapshot.class, new ItemStackSnapshotDeserializer());
        mod.addDeserializer(Location.class, new LocationDeserializer());
        om.registerModule(mod);

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(BaseFilter.ID, new BaseFilter(details, perms));
        om.setFilterProvider(filterProvider);

        om.setAnnotationIntrospector(new AnnotationIntrospector(details));

        return om;
    }
}
