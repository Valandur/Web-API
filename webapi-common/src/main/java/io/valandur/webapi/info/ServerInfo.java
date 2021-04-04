package io.valandur.webapi.info;

import io.leangen.graphql.annotations.GraphQLQuery;

public class ServerInfo {

    @GraphQLQuery(name = "messageOfTheDay", description = "The message of the day set on the server")
    public final String messageOfTheDay;

    @GraphQLQuery(name = "playerCount", description = "The current amount of players on the server")
    public final int playerCount;

    @GraphQLQuery(name = "maxPlayers", description = "The maximum amount of players on the server")
    public final int maxPlayers;

    @GraphQLQuery(name = "onlineMode", description = "True if this server is in online mode, false otherwise")
    public final boolean onlineMode;

    @GraphQLQuery(name = "uptime", description = "The current uptime of the server, measured in milliseconds")
    public final long uptime;

    @GraphQLQuery(name = "minecraftVersion", description = "The minecraft version that the server is running")
    public final String minecraftVersion;

    public ServerInfo(String messageOfTheDay, int playerCount, int maxPlayers, boolean onlineMode, long uptime,
                      String minecraftVersion) {
        this.messageOfTheDay = messageOfTheDay;

        this.playerCount = playerCount;
        this.maxPlayers = maxPlayers;

        this.onlineMode = onlineMode;
        this.uptime = uptime;

        this.minecraftVersion = minecraftVersion;
    }
}
