package valandur.webapi.json;

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
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.block.ConnectedDirectionData;
import org.spongepowered.api.data.manipulator.mutable.block.PoweredData;
import org.spongepowered.api.data.manipulator.mutable.block.RedstonePoweredData;
import org.spongepowered.api.data.manipulator.mutable.entity.*;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.manipulator.mutable.item.SpawnableData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
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
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.json.IJsonService;
import valandur.webapi.api.json.BaseView;
import valandur.webapi.api.json.BaseSerializer;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.cache.entity.CachedEntity;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedCause;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.json.view.block.BlockSnapshotView;
import valandur.webapi.json.view.block.BlockStateView;
import valandur.webapi.json.view.entity.*;
import valandur.webapi.json.view.event.DamageSourceView;
import valandur.webapi.json.view.event.EventView;
import valandur.webapi.json.view.item.*;
import valandur.webapi.json.view.misc.*;
import valandur.webapi.json.view.player.*;
import valandur.webapi.json.view.tileentity.ConnectedDirectionDataView;
import valandur.webapi.json.view.tileentity.PoweredDataView;
import valandur.webapi.json.view.tileentity.RedstonePoweredDataView;
import valandur.webapi.json.view.tileentity.SignDataView;
import valandur.webapi.util.Util;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonService implements IJsonService {

    private Map<Class, BaseSerializer> serializers;
    private Map<String, Class<? extends DataManipulator>> supportedData;


    public void init() {
        Logger logger = WebAPI.getLogger();

        logger.info("Loading serializers...");

        serializers = new HashMap<>();

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

        // Entity
        registerView(AgeableData.class, AgeableDataView.class);
        registerView(CareerData.class, CareerDataView.class);
        registerView(Career.class, CareerView.class);
        registerView(DyeableData.class, DyeableDataView.class);
        registerView(DyeColor.class, DyeColorView.class);
        registerView(FoodData.class, FoodDataView.class);
        registerView(HealthData.class, HealthDataView.class);
        registerView(ShearedData.class, ShearedDataView.class);
        registerView(TameableData.class, TameableDataView.class);
        registerView(TradeOfferData.class, TradeOfferDataView.class);
        registerView(TradeOffer.class, TradeOfferView.class);

        // Event
        registerView(DamageSource.class, DamageSourceView.class);
        registerView(Event.class, EventView.class);

        // Item
        registerView(DurabilityData.class, DurabilityDataView.class);
        registerView(ItemStackSnapshot.class, ItemStackSnapshotView.class);
        registerView(ItemStack.class, ItemStackView.class);
        registerView(ItemType.class, ItemTypeView.class);
        registerView(PotionEffectData.class, PotionEffectDataView.class);
        registerView(PotionEffectType.class, PotionEffectTypeView.class);
        registerView(PotionEffect.class, PotionEffectView.class);
        registerView(SpawnableData.class, SpawnableDataView.class);

        // Misc.
        registerView(Color.class, ColorView.class);
        registerView(CommandSource.class, CommandSourceView.class);
        registerView(Explosion.class, ExplosionView.class);
        registerView(Instant.class, InstantView.class);
        registerView(Vector3d.class, Vector3dView.class);
        registerView(Vector3i.class, Vector3iView.class);

        // Player
        registerView(AchievementData.class, AchievementDataView.class);
        registerView(Achievement.class, AchievementView.class);
        registerView(Ban.class, BanView.class);
        registerView(ExperienceHolderData.class, ExperienceHolderDataView.class);
        registerView(GameModeData.class, GameModeDataView.class);
        registerView(GameMode.class, GameModeView.class);
        registerView(GameProfile.class, GameProfileView.class);
        registerView(JoinData.class, JoinDataView.class);
        registerView(StatisticData.class, StatisticDataView.class);

        // Tile-Entity
        registerView(ConnectedDirectionData.class, ConnectedDirectionDataView.class);
        registerView(PoweredData.class, PoweredDataView.class);
        registerView(RedstonePoweredData.class, RedstonePoweredDataView.class);
        registerView(SignData.class, SignDataView.class);


        // Data
        supportedData = new ConcurrentHashMap<>();
        supportedData.put("achievements", AchievementData.class);
        supportedData.put("age", AgeableData.class);
        supportedData.put("career", CareerData.class);
        supportedData.put("connectedDirection", ConnectedDirectionData.class);
        supportedData.put("durability", DurabilityData.class);
        supportedData.put("dye", DyeableData.class);
        supportedData.put("experience", ExperienceHolderData.class);
        supportedData.put("food", FoodData.class);
        supportedData.put("gameMode", GameModeData.class);
        supportedData.put("health", HealthData.class);
        supportedData.put("joined", JoinData.class);
        supportedData.put("potionEffects", PotionEffectData.class);
        supportedData.put("powered", PoweredData.class);
        supportedData.put("redstonePower", RedstonePoweredData.class);
        supportedData.put("sheared", ShearedData.class);
        supportedData.put("sign", SignData.class);
        supportedData.put("spawn", SpawnableData.class);
        supportedData.put("statistics", StatisticData.class);
        supportedData.put("tamed", TameableData.class);
        supportedData.put("trades", TradeOfferData.class);

        // Load extra serializers
        logger.info("Loading custom serializers...");

        WebAPI.getExtensionService().loadPlugins("serializers", BaseSerializer.class, serializerClass -> {
            try {
                BaseSerializer serializer = serializerClass.newInstance();

                // Check if we already have a serializer for that class
                BaseSerializer prev = serializers.remove(serializer.getHandledClass());
                if (prev != null) {
                    logger.info("    Replacing existing serializer for '" + serializer.getHandledClass().getName() + "'");
                }

                serializers.put(serializer.getHandledClass(), serializer);
            } catch (IllegalAccessException | InstantiationException e) {
                logger.warn("   Could not instantiate serializer '" + serializerClass.getName() + "': " + e.getMessage());
                if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
            }
        });

        logger.info("Done loading serializers");
    }

    @Override
    public <T> void registerCache(Class<? extends T> handledClass, Class<? extends ICachedObject<T>> cacheClass) {
        serializers.put(handledClass, new valandur.webapi.json.BaseSerializer(handledClass, cacheClass));
    }
    @Override
    public <T> void registerView(Class<? extends T> handledClass, Class<? extends BaseView<T>> viewClass) {
        serializers.put(handledClass, new valandur.webapi.json.BaseSerializer(handledClass, viewClass));
    }

    @Override
    public Map<String, Class<? extends DataManipulator>> getSupportedData() {
        return supportedData;
    }

    @Override
    public String toString(Object obj, boolean details, TreeNode<String, Boolean> perms) {
        ObjectMapper mapper = getDefaultObjectMapper(details, perms);

        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    @Override
    public JsonNode toJson(Object obj, boolean details, TreeNode<String, Boolean> perms) {
        ObjectMapper mapper = getDefaultObjectMapper(details, perms);
        return mapper.valueToTree(obj);
    }
    @Override
    public JsonNode toJson(String json, TreeNode<String, Boolean> perms) throws IOException {
        ObjectMapper mapper = getDefaultObjectMapper(true, perms);
        return mapper.readTree(json);
    }
    @Override
    public JsonNode toJson(Reader reader, TreeNode<String, Boolean> perms) throws IOException {
        ObjectMapper mapper = getDefaultObjectMapper(true, perms);
        return mapper.readTree(reader);
    }

    @Override
    public <T> T toObject(String content, Class<T> clazz, TreeNode<String, Boolean> perms) throws IOException {
        ObjectMapper mapper = getDefaultObjectMapper(true, perms);
        return mapper.readValue(content, clazz);
    }
    @Override
    public <T> T toObject(String content, JavaType type, TreeNode<String, Boolean> perms) throws IOException {
        ObjectMapper mapper = getDefaultObjectMapper(true, perms);
        return mapper.readValue(content, type);
    }
    @Override
    public <T> T toObject(JsonNode content, Class<T> clazz, TreeNode<String, Boolean> perms) throws IOException {
        ObjectMapper mapper = getDefaultObjectMapper(true, perms);
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

    private ObjectMapper getDefaultObjectMapper(boolean details, TreeNode<String, Boolean> perms) {
        if (perms == null) {
            throw new NullPointerException("Permissions may not be null");
        }

        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        /*om.enable(
                SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID
        );
        om.disable(
                SerializationFeature.FAIL_ON_EMPTY_BEANS
        );

        om.enable(
                MapperFeature.AUTO_DETECT_CREATORS,
                MapperFeature.AUTO_DETECT_FIELDS
        );
        om.disable(
                MapperFeature.AUTO_DETECT_SETTERS,
                MapperFeature.AUTO_DETECT_GETTERS,
                MapperFeature.AUTO_DETECT_IS_GETTERS
        );*/

        SimpleModule mod = new SimpleModule();
        for (Map.Entry<Class, BaseSerializer> entry : serializers.entrySet()) {
            mod.addSerializer(entry.getKey(), entry.getValue());
        }
        om.registerModule(mod);

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(BaseFilter.ID, new BaseFilter(details, perms));
        om.setFilterProvider(filterProvider);

        om.setAnnotationIntrospector(new AnnotationIntrospector(details));

        return om;
    }
}
