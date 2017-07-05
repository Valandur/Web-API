package valandur.webapi.api.server;

import java.util.List;
import java.util.Map;

/**
 * This service provides information about the minecraft server.
 */
public interface IServerService {

    /**
     * Gets the properties that are in the server.properties file of the minecraft server.
     * @return The properties of the minecraft server.
     */
    Map<String, String> getProperties();

    /**
     * Sets a property for the minecraft server.
     * @param key The key of the property.
     * @param value The new value of the property.
     */
    void setProperty(String key, String value);

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
