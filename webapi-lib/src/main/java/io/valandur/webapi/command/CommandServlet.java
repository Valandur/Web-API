package io.valandur.webapi.command;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.valandur.webapi.chat.ChatHistoryItem;
import io.valandur.webapi.web.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Singleton
@Path("command")
@Tag(name = "Command", description = CommandServlet.classDescr)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommandServlet extends BaseServlet {

  protected static final String classDescr = "Get & execute commands on the server";
  private static final String getCommandHistoryDescr = "Get the history of executed commands on the server";
  private static final String executeCommandDescr = "Execute a command on the server";

  @GET
  @GraphQLNonNull
  @GraphQLQuery(name = "commandHistory", description = getCommandHistoryDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The history of commands executed on the server",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = CommandHistoryItem.class))))
  public List<CommandHistoryItem> getCommandHistory() {
    return commandService.getCommandHistory();
  }

  @POST
  @GraphQLMutation(name = "executeCommand", description = executeCommandDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The command was executed successfully")
  public void sendChatMessage(
      @GraphQLNonNull @GraphQLArgument(name = "command", description = "The command to execute") String command) {
    commandService.executeCommand(command);
  }
}
