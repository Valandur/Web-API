# Web-API API

The Web-API API offers an interface for other plugins to interact with the Web-API. This allows
other plugins to provide their own endpoints for data without much effort.


## Table of Contents
1. [Setup](#setup)
1. [Servlets](#servlets)
    1. [Example](#servlet-example)
1. [Endpoints](#endpoints)
    1. [Example](#endpoint-example)
1. [Serializing Data](#serializing)


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

Your servlet class needs to extend `BaseServlet`, which is the base servlet that provides
some functionality for your servlet to use.

You must also add the `@Servlet(basePath = "")` annotation to the class. This annotation
tells the Web-API that your class is indeed a valid servlet, and which base route to use. 

- The `basePath` specifies at which path your servlet will be available, and all the other 
routes in your servlet will be relative to this route.The `basePath` does **not** require any slashes `/` and may **not** contain any path parameters (see below).

Servlets may define a `public static void onRegister()` method which will get called by the WebAPI
when the servlet is registered. This is only done once, even if the user reloads the plugins
on the server. This is the best place to register any custom serializers that your servlets uses:
```java
public static void onRegister() {
    WebAPIAPI.getJsonService().ifPresent(srv -> {
        srv.registerSerializer(MyData.class, MyDataSerializer.class);
    });
}
```


<a name="servlet-example"></a>
### Example

```java
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Servlet;

@Servlet(basePath = "my")
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

The methods must also be marked with the `@Endpoint(method = HttpMethod, path = "", perm = "")`
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
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.api.cache.world.ICachedWorld;

@Servlet(basePath = "my")
public class MyServlet extends BaseServlet {
    
    @Endpoint(method = HttpMethod.GET, path = "/test/:world/:myInt", perm = "test")
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


<a name="serializing"></a>
## Serializing Data

The Web-API automatically turns java objects into json. Sometimes this can be a little
overwhelming to do for a program, so you can help it by giving it hints as to what it should
include, and how.

There are two ways to define how your java objects are turned into json:


## 1. Annotations  

By default all **public fields** and **public methods that are getters** (begin with `get` and 
have a return value and no arguments) are used for serialization. Also methods that return
boolean values and begin with `is`.

You can use the annotations of the jackson library to further specify how your data should be
serialized. The most important annotations are:
- `JsonProperty` with which you can define how your property should be serialized 
  (name, default value, etc.)
- `JsonIgnore` which you can use to ignore a field/method completely
- `JsonDetails` which you can use to specify that a field should only be present when the user
wishes to retrieve all of the object data. *This annotation is from the Web-API API itself, not
from the jackson library.* 
- `JsonAnyGetter` which can be used on a method that returns a map from string to object,
which contains all/additional properties of the current object.
- `JsonValue` which can be used on a field or method to serialize the whole object as the
marked field or method.

You can find more information about all the jackson annotations in their 
[official documentation](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations).

Simply add these annotations to your java objects that you return in your endpoints, and Web-API
will take care of serializing your data to json.


## 2. Views

Views are used when you don't have direct access to a certain class (for example when serialing
sponge/minecraft objects), or annotations are too complicated to use.

A view defines basically a "copy" of your data object, which contains only the data which will
be serialized. This can also be used for caching means, if your data object is not thread safe.

Views can also take advantage of all the annotations listed above, but should be built with
json data already in mind, so should not need many of them.

> The Web-API already provides views for most sponge related data objects, such as Worlds, 
Players, Entities, Inventories, Blocks and more.

Your view must extend the class `BaseView<T>` of the Web-API API, and provide the class for
which you are providing a view as the type argument, and provide a matching constructor.

Following is an example View for Sponge's `BlockState`:

```java
package valandur.webapi.json.view.block;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import valandur.webapi.api.json.JsonDetails;
import valandur.webapi.api.json.BaseView;

import java.util.HashMap;
import java.util.Map;

public class BlockStateView extends BaseView<BlockState> {

    public BlockType type;


    public BlockStateView(BlockState value) {
        super(value);

        this.type = value.getType();
    }

    @JsonDetails
    public Map<String, Object> getData() {
        HashMap<String, Object> data = new HashMap<>();
        for (Map.Entry<BlockTrait<?>, ?> entry : value.getTraitMap().entrySet()) {
            data.put(entry.getKey().getName(), entry.getValue());
        }
        return data;
    }
}
```
As you can see it has the sponge `BlockState` class as it's type argument, and provides a
constructor that matches the type argument.

Since `type` is a public field it will automatically get serialized
(The Web-API already provides a view for Sponge's `CatalogType`).

The `getData()` method will be serialized as well (since it starts with `get` and has no
arguments and a return type other than void), but only if the details for the object are
requested (since the method is annotated with `JsonDetails`). This prevents the system
from going through all the traits of a block, unless they are actually required.
