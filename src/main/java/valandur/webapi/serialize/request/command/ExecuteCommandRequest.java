package valandur.webapi.serialize.request.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import valandur.webapi.servlet.CmdServlet;

public class ExecuteCommandRequest {

    @JsonDeserialize
    private String command;
    public String getCommand() {
        return command;
    }

    @JsonDeserialize
    private String name;
    public String getName() {
        return name != null ? name : "Web-API";
    }

    @JsonDeserialize
    private Integer waitTime;
    public Integer getWaitTime() {
        return waitTime != null ? waitTime : CmdServlet.CMD_WAIT_TIME;
    }

    @JsonDeserialize
    private Integer waitLines;
    public Integer getWaitLines() {
        return waitLines != null ? waitLines : 0;
    }

    @JsonDeserialize
    private Boolean hideInConsole;
    public Boolean isHiddenInConsole() {
        return hideInConsole != null ? hideInConsole : false;
    }
}
