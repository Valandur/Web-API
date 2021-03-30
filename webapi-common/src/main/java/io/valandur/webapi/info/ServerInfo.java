package io.valandur.webapi.info;

public class ServerInfo {
    public final String messageOfTheDay;

    public final int playerCount;
    public final int maxPlayers;

    public final boolean onlineMode;

    public final long uptime;

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
