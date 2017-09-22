# Web-API API

The Web-API API offers an interface for other plugins to interact with the Web-API. This allows
other plugins to provide their own endpoints for data without much effort.


## Table of Contents
1. [Setup](#setup)
1. [Servlets](#servlets)
    1. [Example](#servlet-example)
1. [Endpoints](#endpoints)
    1. [Example](#endpoint-example)
1. [Requests](#requests)
1. [Responses](#responses)


<a name="setup"></a>
## Setup

> You have to register any servlets you create with the WebAPI

The easiest way to do this is to use `WebAPIAPI.getServletService()`, which will return an optional
containing the ServletService. If the optional is empty than the WebAPI plugin is not present or 
loaded on the server. If the ServletService is present you can use 
`service.registerServlet(Class<? extends WebAPIBaseServlet> servlet)` to register your servlet class
with the WebAPI.
```java
public void onInitialization(GameInitializationEvent event) {
    ...
    
    Optional<IServletService> optSrv = WebAPIAPI.getServletService();
    if (optSrv.isPresent()) {
        IServletService srv = optSrv.get();
        srv.registerServlet(MyServlet.class);
    }
    
    ...
}
```

> This should be done in the initialization phase of your plugin, before the server starts!


<a name="servlets"></a>
## Servlets
Servlets are a collection of routes. This allows you to easily group all data that belongs together.

At runtime the Web-API will create an instance of your servlet and use it to server the routes
that you specified.

> Note that your servlet is re-initialized (a new class instance is created) when the Web-API 
performs a reload (e.g. through `sponge plugins reload`), so any initialization that your routes
require should be handled in your constructor.

Your servlet class needs to extend `WebAPIBaseServlet`, which is the base servlet that provides
some functionality for your servlet to use.

You must also add the `@WebAPIServlet(basePath = "")` annotation to the class. This annotation
tells the Web-API that your class is indeed a valid servlet, and which base route to use. 

- The `basePath` specifies at which path your servlet will be available, and all the other 
routes in your servlet will be relative to this route.The `basePath` does **not** require any slashes `/` and may **not** contain any path parameters (see below).

Servlets may define a `public static void onRegister()` method which will get called by the WebAPI
when the servlet is registered. This is only done once, even if the user reloads the plugins
on the server. This is the best place to register any custom serializers that your servlets uses:
```java
public static void onRegister() {
    JsonService json = WebAPI.getJsonService();
    json.registerSerializer(MyData.class, MyDataSerializer.class);
}
```


<a name="servlet-example"></a>
### Example

```java
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;

@WebAPIServlet(basePath = "my")
public class MyServlet extends WebAPIBaseServlet {
    
}
```

This code sample creates a servlet that operate on the route `/api/my/*`, but does not yet
contain any actual endpoints that do anything.


<a name="endpoints"></a>
## Endpoints
Endpoints are the actual routes that handle the data processing within a servlet.

Your routes must be methods/functions that return `void` and have at least one parameter of type
`IServletData`. The name of the method is up to you, and should probably be something that 
describes what the endpoint does.

The methods must also be marked with the `@WebAPIEndpoint(method = HttpMethod, path = "", perm = "")`
annotation. This annotation tells the Web-API that the provided method is actually an endpoint,
and what type of endpoint it is.

- The `method` argument specifies what HttpMethod this endpoint listens to 
(check `org.eclipse.jetty.http.HttpMethod`. Includes `GET`, `POST` and many more).

- The `perm` argument tells the Web-API which permissions node is required for this endpoint.

- `path` is the path of the endpoint relative to the `basePath` of the servlet. The path may
contain path parameters, which are parsed and passed to the method by the Web-API.  
A path parameter is specified with a leading double point `:` e.g. `path="/test/:arg"`. In this
case the path parameter is called `arg`. You can access this path parameter in two different ways:
    - Using `data.getPathParam("arg")`, where `data` is the `IServletData` parameter of the method
    that is always required to be there  
    **or**
    - By adding an additional argument to the method with the type of the parameter 
    (example below). This allows the Web-API to parse certain types of parameters and display 
    an error to the user in case the parameter is not of the correct type.  
> The Web-API supports the following types of path parameters by using them as method arguments:
**Integer/int, Long/long, Double/double, String, Boolean/boolean, UUID, 
ICachedEntity, ICachedPlayer, ICachedWorld**  
For ICachedEntity, ICachedPlayer and ICachedWorld the parameter is assumed to be the entity's,
player's or world's **UUID**, and the Web-API tries to find the requested object by that.


<a name="endpoint-example"></a>
### Example

```java
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;

@WebAPIServlet(basePath = "my")
public class MyServlet extends WebAPIBaseServlet {
    
    @WebAPIEndpoint(method = HttpMethod.GET, path = "/test/:world/:myInt", perm = "test")
    public void testRoute(IServletData data, ICachedWorld world) {
        // Get the path parameter we specified above. Note that when accessing the path
        // parameters like this they are not parsed but returned as a String, and it is
        // up to you to do an pre-processing, like checking for valid numbers etc.
        data.addJson("int", data.getPathParam("myInt"), false);
        
        // Adding the path parameter to the argument list allows the Web-API to parse
        // the parameter to the according type. In case of players, entities and worlds
        // the parameter is assumed to be the player's/entity's/world's UUID. An error
        // is automatically returned to the user in case the player/entity/world is not 
        // found or the parameter is otherwise invalid. This means your method does not 
        // have to worry about invalid/missing parameters. 
        data.addJson("world", world, true);
        
        // Everything is ok
        data.addJson("ok", true, false);
    }
}
```

This example reuses the servlet we defined above.  
It adds the endpoint `testRoute`, which will be available at `/api/my/test/:world/:myInt`.
The permissions for this endpoint are handled in the `test` permissions node, under the `my`
endpoint.


<a name="requests"></a>
## Requests


<a name="responses"></a>
## Responses
