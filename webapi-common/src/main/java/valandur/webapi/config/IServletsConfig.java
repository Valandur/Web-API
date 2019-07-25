package valandur.webapi.config;

public interface IServletsConfig {
    boolean isBlockEnabled();
    boolean isChunkEnabled();
    boolean isCmdEnabled();
    boolean isEconomyEnabled();
    boolean isEntityEnabled();
    boolean isHistoryEnabled();
    boolean isInfoEnabled();
    boolean isInteractiveMessageEnabled();
    boolean isMapEnabled();
    boolean isPermissionEnabled();
    boolean isPlayerEnabled();
    boolean isPluginEnabled();
    boolean isRecipeEnabled();
    boolean isRegistryEnabled();
    boolean isServerEnabled();
    boolean isTileEntityEnabled();
    boolean isUserEnabled();
    boolean isWorldEnabled();
    IServletsIntegrationConfig getIntegrations();

    interface IServletsIntegrationConfig {
        boolean isActiveTimeEnabled();
        boolean isCmdSchedulerEnabled();
        boolean isGWMCratesEnabled();
        boolean isHuskyCratesEnabled();
        boolean isMMCRestrictEnabled();
        boolean isMMCTicketsEnabled();
        boolean isNucleusEnabled();
        boolean isRedProtectEnabled();
        boolean isUniversalMarketEnabled();
        boolean isVillagerShopsEnabled();
        boolean isWebBooksEnabled();
    }
}
