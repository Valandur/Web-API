package valandur.webapi.api.server;

import java.util.List;

/**
 * This service provides information about the minecraft server.
 */
public interface IServerService {

    /**
     * Gets the number of data entries currently recorded.
     * @return The number of data entries that are available.
     */
    int getNumEntries();

    /**
     * Gets a history of the average TPS of the minecraft server.
     * @return A list containing measurements of the TPS.
     */
    List<IServerStat<Double>> getAverageTps();
    /**
     * Gets a history of the amount of players that were online on the minecraft server.
     * @return A list containing measurements of the amount of players online.
     */
    List<IServerStat<Integer>> getOnlinePlayers();

    /**
     * Gets a history of the average load of the cpu.
     * @return A list containing the measurements of the average load of the cpu.
     */
    List<IServerStat<Double>> getCpuLoad();

    /**
     * Gets a history of the average memory load.
     * @return A list containing the measurements of the average memory load.
     */
    List<IServerStat<Double>> getMemoryLoad();

    /**
     * Gets a history of the average disk usage.
     * @return A list containing measurements of the average disk usage.
     */
    List<IServerStat<Double>> getDiskUsage();
}
