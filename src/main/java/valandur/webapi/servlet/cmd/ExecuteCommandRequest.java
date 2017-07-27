package valandur.webapi.servlet.cmd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class ExecuteCommandRequest {

    @JsonDeserialize
    private String command;
    public String getCommand() {
        return command;
    }

    @JsonDeserialize
    private String name;
    public String getName() {
        return name;
    }

    @JsonDeserialize
    private Integer waitTime;
    public Integer getWaitTime() {
        return waitTime;
    }

    @JsonDeserialize
    private Integer waitLines;
    public Integer getWaitLines() {
        return waitLines;
    }

    @JsonDeserialize
    private boolean hideInConsole;
    public boolean isHiddenInConsole() {
        return hideInConsole;
    }
}
