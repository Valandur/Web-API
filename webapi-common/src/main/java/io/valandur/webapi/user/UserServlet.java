package io.valandur.webapi.user;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.valandur.webapi.BaseServlet;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Singleton
@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserServlet extends BaseServlet {

    @GET
    @GraphQLQuery(name = "users")
    public Collection<User> getUsers() throws ExecutionException, InterruptedException {
        return webapi.runOnMain(() -> webapi.getUsers());
    }
    
    @GET
    @Path("{user}")
    @GraphQLQuery(name = "user")
    public User getUser(
            @PathParam("user") @GraphQLArgument(name = "uuid", description = "The UUID of the user") String uuidString)
            throws ExecutionException, InterruptedException {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return webapi.runOnMain(() -> webapi.getUser(uuid));
        } catch (IllegalArgumentException ignored) {
            throw new BadRequestException("Invalid uuid");
        }
    }
}
