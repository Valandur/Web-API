package valandur.webapi.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.entity.*;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.manipulator.mutable.item.SpawnableData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.world.*;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.weather.Weather;
import valandur.webapi.WebAPI;
import valandur.webapi.json.serializers.WebAPISerializer;
import valandur.webapi.json.serializers.block.*;
import valandur.webapi.json.serializers.entity.*;
import valandur.webapi.json.serializers.entity.TradeOfferSerializer;
import valandur.webapi.json.serializers.event.CauseSerializer;
import valandur.webapi.json.serializers.event.EventSerializer;
import valandur.webapi.json.serializers.general.*;
import valandur.webapi.json.serializers.item.*;
import valandur.webapi.json.serializers.player.*;
import valandur.webapi.json.serializers.tileentity.SignDataSerializer;
import valandur.webapi.json.serializers.tileentity.TileEntitySerializer;
import valandur.webapi.json.serializers.world.*;
import valandur.webapi.misc.Util;
import valandur.webapi.misc.WebAPIDiagnosticListener;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class JsonConverter {

    private static Map<Class, JsonSerializer> serializers;
    private static Map<String, Class> supportedData;
    private static Map<String, String> relocatedPackages;

    public static void initSerializers() {
        serializers = new HashMap<>();

        // General
        serializers.put(Vector3d.class, new Vector3dSerializer());
        serializers.put(Vector3i.class, new Vector3iSerializer());

        // Block
        serializers.put(BlockVolume.class, new BlockVolumeSerializer());
        serializers.put(BlockState.class, new BlockStateSerializer());

        // Entity
        serializers.put(AgeableData.class, new AgeableDataSerializer());
        serializers.put(CareerData.class, new CareerDataSerializer());
        serializers.put(Career.class, new CareerSerializer());
        serializers.put(DyeableData.class, new DyeableDataSerializer());
        serializers.put(Entity.class, new EntitySerializer());
        serializers.put(FoodData.class, new FoodDataSerializer());
        serializers.put(HealthData.class, new HealthDataSerializer());
        serializers.put(ShearedData.class, new ShearedDataSerializer());
        serializers.put(TameableData.class, new TameableDataSerializer());
        serializers.put(TradeOfferData.class, new TradeOfferDataSerializer());
        serializers.put(TradeOffer.class, new TradeOfferSerializer());

        // Event
        serializers.put(Cause.class, new CauseSerializer());
        serializers.put(Event.class, new EventSerializer());

        // Item
        serializers.put(DurabilityData.class, new DurabilityDataSerializer());
        serializers.put(Inventory.class, new InventorySerializer());
        serializers.put(ItemStack.class, new ItemStackSerializer());
        serializers.put(ItemStackSnapshot.class, new ItemStackSnapshotSerializer());
        serializers.put(PotionEffectData.class, new PotionEffectDataSerializer());
        serializers.put(PotionEffect.class, new PotionEffectSerializer());
        serializers.put(SpawnableData.class, new SpawnableDataSerializer());

        // Player
        serializers.put(AchievementData.class, new AchievementDataSerializer());
        serializers.put(Achievement.class, new AchievementSerializer());
        serializers.put(Ban.Profile.class, new BanSerializer());
        serializers.put(ExperienceHolderData.class, new ExperienceHolderDataSerializer());
        serializers.put(GameModeData.class, new GameModeDataSerializer());
        serializers.put(GameMode.class, new GameModeSerializer());
        serializers.put(GameProfile.class, new GameProfileSerializer());
        serializers.put(JoinData.class, new JoinDataSerializer());
        serializers.put(PlayerConnection.class, new PlayerConnectionSerializer());
        serializers.put(Player.class, new PlayerSerializer());
        serializers.put(StatisticData.class, new StatisticDataSerializer());

        // Tile-Entity
        serializers.put(SignData.class, new SignDataSerializer());
        serializers.put(TileEntity.class, new TileEntitySerializer());

        // World
        serializers.put(Difficulty.class, new DifficultySerializer());
        serializers.put(Dimension.class, new DimensionSerializer());
        serializers.put(DimensionType.class, new DimensionTypeSerializer());
        serializers.put(GeneratorType.class, new GeneratorTypeSerializer());
        serializers.put(Weather.class, new WeatherSerializer());
        serializers.put(World.class, new WorldSerializer());
        serializers.put(WorldBorder.class, new WorldBorderSerializer());



        // Data
        supportedData = new HashMap<>();
        supportedData.put("achievements", AchievementData.class);
        supportedData.put("career", CareerData.class);
        supportedData.put("durability", DurabilityData.class);
        supportedData.put("dye", DyeableData.class);
        supportedData.put("experience", ExperienceHolderData.class);
        supportedData.put("food", FoodData.class);
        supportedData.put("gameMode", GameModeData.class);
        supportedData.put("health", HealthData.class);
        supportedData.put("joined", JoinData.class);
        supportedData.put("potionEffects", PotionEffectData.class);
        supportedData.put("sheared", ShearedData.class);
        supportedData.put("sign", SignData.class);
        supportedData.put("spawn", SpawnableData.class);
        supportedData.put("statistics", StatisticData.class);
        supportedData.put("tameable", TameableData.class);
        supportedData.put("trades", TradeOfferData.class);


        // Relocated packages
        relocatedPackages = new HashMap<>();
        relocatedPackages.put("import org.eclipse.jetty",     "import valandur.webapi.shadow.org.eclipse.jetty");
        relocatedPackages.put("import com.fasterxml.jackson", "import valandur.webapi.shadow.fasterxml.jackson");
        relocatedPackages.put("import javax.servlet",         "import valandur.webapi.shadow.javax.servlet");
        relocatedPackages.put("import org.reflections",       "import valandur.webapi.shadow.org.reflections");
        relocatedPackages.put("import net.jodah",             "import valandur.webapi.shadow.net.jodah");
    }
    public static void loadExtraSerializers() {
        Logger logger = WebAPI.getInstance().getLogger();
        logger.info("Loading additional serializers...");

        // Get root directory
        File root = new File("webapi");
        File folder = new File("webapi/serializers");
        if (!folder.exists() && !folder.mkdirs()) {
            logger.warn("Could not create folder for additional serializers");
            return;
        }

        List<File> files = null;
        try {
            files = Files.walk(Paths.get(root.toURI()))
                    .filter(Files::isRegularFile)
                    .filter(f -> f.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        logger.info("Found " + files.size() + " serializer files in " + root.getAbsolutePath());
        if (files.size() == 0) {
            return;
        }

        // Setup java compiler
        ClassLoader currentCl = Thread.currentThread().getContextClassLoader();
        URL[] urls = ((URLClassLoader) currentCl).getURLs();
        String classpath = Arrays.stream(urls).map(URL::getPath).filter(Objects::nonNull).collect(Collectors.joining(";"));

        WebAPIDiagnosticListener diag = new WebAPIDiagnosticListener();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            logger.warn("You need to install & use a JDK to support custom serializers. ");
            return;
        }

        StandardJavaFileManager fm = compiler.getStandardFileManager(diag, null, null);
        List<String> optionList = Arrays.asList("-classpath", classpath);

        // Compile, load and instantiate compiled class.
        for (File file : files) {
            String logFile = file.getAbsolutePath().replace(".java", ".log");
            diag.startLog(logFile);

            try {
                logger.info("  - " + file.getName());

                // Read file to check for some basic things like package and shadowed references
                String fileContent = new String(Files.readAllBytes(file.toPath()));

                if (!fileContent.contains("package serializers;")) {
                    logger.error("   The class must be in the 'serializers' package.");
                    continue;
                }

                int start = fileContent.indexOf("class ") + 6;
                int end = fileContent.indexOf(" ", start);
                String cName = fileContent.substring(start, end);
                if (!cName.equalsIgnoreCase(file.getName().substring(0, file.getName().length() - 5))) {
                    logger.error("   File name '" + file.getName().substring(0, file.getName().length() - 5) + "' must match class name '" + cName + "'");
                    continue;
                }

                if (!fileContent.contains("extends WebAPISerializer<")) {
                    logger.error("   Class must extend WebAPISerializer, and must provide the event class as a " +
                            "generic parameter, e.g: WebAPISerializer<InteractBlockEvent>");
                    continue;
                }

                // Replace shadowed references
                for (Map.Entry<String, String> entry : relocatedPackages.entrySet()) {
                    if (WebAPI.getInstance().isDevMode())
                        fileContent = fileContent.replace(entry.getValue(), entry.getKey());
                    else
                        fileContent = fileContent.replace(entry.getKey(), entry.getValue());
                }

                // Write back to file
                Files.write(file.toPath(), fileContent.getBytes(), StandardOpenOption.CREATE);

                // Compile the file
                Iterable<? extends JavaFileObject> compilationUnits = fm.getJavaFileObjectsFromFiles(Collections.singletonList(file));
                JavaCompiler.CompilationTask task = compiler.getTask(null, fm, diag, optionList, null, compilationUnits);

                boolean res = task.call();

                if (!res) {
                    logger.error("   Compilation failed. See the log file at " + logFile + " for details");
                    continue;
                }

                String className = file.getName().substring(0, file.getName().length() - 5);

                // Load the class
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()}, currentCl);
                Class<?> cls = Class.forName("serializers." + className, true, classLoader);
                if (!WebAPISerializer.class.isAssignableFrom(cls)) {
                    logger.error("   Must extend " + WebAPISerializer.class.getName());
                    continue;
                }

                // Instantiate
                WebAPISerializer instance = (WebAPISerializer)cls.newInstance();

                // Get handled class
                Class forClass = instance.getHandledClass();
                try {
                    Field f = cls.getField("forClass");
                    forClass = (Class) f.get(null);
                } catch (NoSuchFieldException ignored) {}

                // Add to serializers
                serializers.put(forClass, instance);
                logger.info("    -> " + forClass.getName());
            } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                logger.error("   Error. See the log file at " + logFile + " for details");
                diag.writeException(e);
            }

            diag.stopLog();
        }

        diag.stopLog();

        logger.info("Done loading additional serializers");
    }

    /**
     * Converts an object directly to a json string. EXCLUDES details.
     * @param obj The object to convert to json.
     * @return The json string representation of the object.
     */
    public static String toString(Object obj) {
        return toString(obj, false);
    }

    /**
     * Converts an object directly to a json string. Includes details if specified.
     * @param obj The object to convert to json.
     * @param details False if only marked properties/methods should be included, true otherwise.
     * @return The json string representation of the object.
     */
    public static String toString(Object obj, boolean details) {
        ObjectMapper om = getDefaultObjectMapper();

        if (!details) {
            om.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS, MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
        }

        try {
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    /**
     * Converts an object to json using the default object mapper. EXCLUDES details.
     * @param obj The object to convert to json.
     * @return The json representation of the object.
     */
    public static JsonNode toJson(Object obj) {
        return toJson(obj, false);
    }

    /**
     * Converts an object to json using the default object mapper. Includes details if specified.
     * @param obj The object to convert to json
     * @param details False if only marked properties/methods should be included, true otherwise.
     * @return The json representation of the object.
     */
    public static JsonNode toJson(Object obj, boolean details) {
        ObjectMapper om = getDefaultObjectMapper();
        if (!details) {
            om.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS, MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
            om.setAnnotationIntrospector(new DisableAnyGetterInspector());
        }
        return om.valueToTree(obj);
    }

    /**
     * Converts a DataHolder to a json object. EXCLUDES details.
     * @param holder The DataHolder to convert to json.
     * @return The json representation of the DataHolder.
     */
    public static Map<String, JsonNode> dataHolderToJson(DataHolder holder) {
        Map<String, JsonNode> nodes = new HashMap<>();

        for (Map.Entry<String, Class> entry : supportedData.entrySet()) {
            Optional<?> m = holder.get(entry.getValue());

            if (!m.isPresent())
                continue;

            nodes.put(entry.getKey(), JsonConverter.toJson(m.get()));
        }

        return nodes;
    }

    /**
     * Get the default object mapper which contains some custom serializers and doesn't fail on empty beans.
     * .@return The default object mapper
     */
    private static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        for (Map.Entry<Class, JsonSerializer> entry : serializers.entrySet()) {
            addSerializer(om, entry.getKey(), entry.getValue());
        }

        return om;
    }
    private static void addSerializer(ObjectMapper mapper, Class clazz, JsonSerializer serializer) {
        SimpleModule mod = new SimpleModule();
        mod.addSerializer(clazz, serializer);
        mapper.registerModule(mod);
    }


    /**
     * Converts a class structure to json. This includes all the fields and methods of the class
     * @param c The class for which to get the json representation.
     * @return A JsonNode representing the class.
     */
    public static JsonNode classToJson(Class c) {
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
}
