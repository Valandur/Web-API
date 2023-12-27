package io.valandur.webapi.common.chat;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.valandur.webapi.chat.ChatMessage;
import io.valandur.webapi.common.web.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Singleton
@Path("chat")
@Tag(name = "Chat", description = ChatServlet.classDescr)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatServlet extends BaseServlet {

  protected static final String classDescr = "Get chat messages on the server";
  private static final String getMessagesDescr = "List all chat messages";
  private static final String sendChatMessageDescr = "Send a chat message to the server";

  @GET
  @GraphQLNonNull
  @GraphQLQuery(name = "chatMessages", description = getMessagesDescr)
  @ApiResponse(
      responseCode = "200",
      description = "An array of chat messages",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = ChatMessage.class))))
  public List<ChatMessage> getMessages() throws ExecutionException, InterruptedException {
    return webapi.runOnMain(() -> chatService.getChatMessages());
  }

  @POST
  @GraphQLMutation(name = "sendChatMessage", description = sendChatMessageDescr)
  @ApiResponse(
      responseCode = "200",
      description = "The message was sent successfully")
  public void sendChatMessage(
      @GraphQLNonNull @GraphQLArgument(name = "message", description = "The message to send") String message)
      throws ExecutionException, InterruptedException {
    webapi.runOnMain(() -> chatService.sendChatMessage(message));
  }
}
