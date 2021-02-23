package io.valandur.webapi.info;

public class ServerInfo {
    public final String messageOfTheDay;

    public final int playerCount;
    public final int maxPlayers;

    public final String address;

    public final boolean onlineMode;

    public final long uptime;
    public final double currentTicksPerSecond;

    public final String minecraftVersion;

    public ServerInfo(String messageOfTheDay, int playerCount, int maxPlayers, String address, boolean onlineMode,
                      long uptime, double currentTicksPerSecond, String minecraftVersion) {
        this.messageOfTheDay = messageOfTheDay;

        this.playerCount = playerCount;
        this.maxPlayers = maxPlayers;

        this.address = address;
        this.onlineMode = onlineMode;
        this.uptime = uptime;
        this.currentTicksPerSecond = currentTicksPerSecond;

        this.minecraftVersion = minecraftVersion;
    }
}
