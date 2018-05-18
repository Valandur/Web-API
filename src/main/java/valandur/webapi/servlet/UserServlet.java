package valandur.webapi.servlet;

import io.swagger.annotations.*;
import valandur.webapi.WebAPI;
import valandur.webapi.api.security.IPermissionService;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.security.AuthenticationProvider;
import valandur.webapi.security.PermissionStruct;
import valandur.webapi.security.SecurityContext;
import valandur.webapi.user.UserPermissionStruct;
import valandur.webapi.user.Users;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("user")
@Api(tags = { "User" }, value = "Authenticate and get user information.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserServlet extends BaseServlet {

    @Context
    HttpServletRequest request;

    @GET
    @Permission("list")
    @ApiOperation(
            value = "List users",
            notes = "Gets a list of all the Web-API users.")
    public List<UserPermissionStruct> getUsers() {
        return Users.getUsers();
    }

    @POST
    @Permission("create")
    @ApiOperation(
            value = "Create a user",
            notes = "Creates a new Web-API user with the specified username and password.")
    public UserPermissionStruct createUser(CreateUserRequest req)
            throws BadRequestException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        if (req.username == null || req.username.length() == 0) {
            throw new BadRequestException("Invalid username");
        }

        if (req.password == null || req.password.length() == 0) {
            throw new BadRequestException("Invalid password");
        }

        if (Users.getUser(req.username).isPresent()) {
            throw new BadRequestException("A user with that username already exists");
        }

        Optional<UserPermissionStruct> optUser =
                Users.addUser(req.username, req.password, IPermissionService.emptyNode());
        if (!optUser.isPresent()) {
            throw new InternalServerErrorException("Could not create user!");
        }

        return optUser.get();
    }

    @PUT
    @Path("/{name}")
    @Permission("modify")
    @ApiOperation(
            value = "Update a user",
            notes = "Changes the properties of a Web-API user")
    public UserPermissionStruct modifyUser(
            @PathParam("name") @ApiParam("The username of the user to delete") String name,
            ModifyUserRequest req)
            throws NotFoundException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<UserPermissionStruct> optUser = Users.getUser(name);
        if (!optUser.isPresent()) {
            throw new NotFoundException("User not found");
        }

        UserPermissionStruct user = optUser.get();

        if (req.permissions != null) {
            user = Users.modifyUser(user, req.permissions);
        }

        return user;
    }

    @DELETE
    @Path("/{name}")
    @Permission("delete")
    @ApiOperation(
            value = "Delete a user",
            notes = "Removes a Web-API user.")
    public UserPermissionStruct deleteUser(
            @PathParam("name") @ApiParam("The username of the user to delete") String name)
            throws NotFoundException {

        Optional<UserPermissionStruct> optUser = Users.removeUser(name);
        if (!optUser.isPresent()) {
            throw new NotFoundException("User not found");
        }

        return optUser.get();
    }

    @GET
    @Path("/me")
    @ApiOperation(
            value = "Check info",
            notes = "Checks to see if the passed api key is still valid and retrieves the user info and " +
                    "permissions associated with this key")
    public PermissionStruct getMe() {
        SecurityContext context = (SecurityContext)request.getAttribute("security");
        return context.getPermissionStruct();
    }

    @POST
    @Path("/login")
    @ApiOperation(
            value = "Login",
            notes = "Tries to acquire an api key with the passed credentials.")
    public PermissionStruct login(AuthRequest req)
            throws ForbiddenException {
        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        Optional<UserPermissionStruct> optPerm = Users.getUser(req.getUsername(), req.getPassword());
        if (!optPerm.isPresent()) {
            WebAPI.getLogger().warn(req.getUsername() + " tried to login from " +
                    request.getAttribute("ip") + " (invalid username or password)");
            throw new ForbiddenException("Invalid username or password");
        }

        UserPermissionStruct perm = optPerm.get();
        String key = Util.generateUniqueId();

        AuthenticationProvider.addTempKey(key, perm);

        WebAPI.getLogger().info(req.getUsername() + " logged in from " + request.getAttribute("ip"));

        return perm.withKey(key);
    }

    @POST
    @Permission
    @Path("/logout")
    @ApiOperation(
            value = "Logout",
            notes = "Invalidate the current API key, logging out the active user.")
    public PermissionStruct logout()
            throws ForbiddenException {
        SecurityContext context = (SecurityContext)request.getAttribute("security");
        AuthenticationProvider.removeTempKey(context.getPermissionStruct().getKey());

        WebAPI.getLogger().info(context.getPermissionStruct().getName() + " logged out");

        return context.getPermissionStruct();
    }

    @GET
    @Permission
    @Path("/logout")
    public Response logoutRedirect(
            @QueryParam("redirect") @ApiParam("The URL the client should be redirect to after logout") String redirect)
            throws ForbiddenException {
        SecurityContext context = (SecurityContext)request.getAttribute("security");
        AuthenticationProvider.removeTempKey(context.getPermissionStruct().getKey());

        WebAPI.getLogger().info(context.getPermissionStruct().getName() + " logged out");

        return Response.status(Response.Status.FOUND).build();
    }


    @ApiModel("AuthenticationRequest")
    public static class AuthRequest {

        private String username;
        @ApiModelProperty(value = "The username of the user", required = true)
        public String getUsername() {
            return username;
        }

        private String password;
        @ApiModelProperty(value = "The password of the user", required = true)
        public String getPassword() {
            return password;
        }
    }

    @ApiModel("CreateUserRequest")
    public static class CreateUserRequest {

        private String username;
        @ApiModelProperty(value = "The username of the user", required = true)
        public String getUsername() {
            return username;
        }

        private String password;
        @ApiModelProperty(value = "The password of the user", required = true)
        public String getPassword() {
            return password;
        }
    }

    @ApiModel("ModifyUserRequest")
    public static class ModifyUserRequest {

        private TreeNode permissions;
        @ApiModelProperty(value = "The permissions of the user")
        public TreeNode getPermissions() {
            return permissions;
        }
    }
}
