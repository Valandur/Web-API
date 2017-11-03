package valandur.webapi.api.server;

import java.util.List;

/**
 * This service provides information about the minecraft server.
 */
public interface IServerService {

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
}
