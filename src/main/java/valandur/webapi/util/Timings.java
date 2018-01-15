package valandur.webapi.util;

import co.aikar.timings.Timing;
import valandur.webapi.WebAPI;

public class Timings {
    public static final Timing RUN_ON_MAIN =
            co.aikar.timings.Timings.of(WebAPI.getInstance(), "Run on main");
    public static final Timing STARTUP =
            co.aikar.timings.Timings.of(WebAPI.getInstance(), "Startup");
    public static final Timing CACHE_WORLD =
            co.aikar.timings.Timings.of(WebAPI.getInstance(), "Cache world");
    public static final Timing CACHE_PLAYER =
            co.aikar.timings.Timings.of(WebAPI.getInstance(), "Cache player");
    public static final Timing CACHE_ENTITY =
            co.aikar.timings.Timings.of(WebAPI.getInstance(), "Cache entity");
    public static final Timing CACHE_TILE_ENTITY =
            co.aikar.timings.Timings.of(WebAPI.getInstance(), "Cache tile-entity");
}
