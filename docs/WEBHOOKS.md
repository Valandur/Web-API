# Web-API WebHooks
WebHooks are ways to subscribe to the Web-API to get informed when certain events happen on the
server. Hooks can be configured in the `config/webapi/hooks.conf`. The configuration options available
will be described in this documentation.


## Table of Contents
1. [General settings](#general)
1. [Hook types](#types)
1. [Event hooks](#event)
1. [Custom event hooks](#custom-event)
1. [Command hooks](#command)
1. [Filter](#filters)
1. [Hook responses](#responses)


<a name="general"></a>
## General settings

Each WebHook supports various configuration settings, which are described below.

- The `address` defines which endpoint is contacted when an event occurs.
    ```yaml
    address="http://127.0.0.1:25000/"
    ```
- The `method` defines which HTTP Verb is used to contact the server. This can be either *GET*,
    *PUT*, *POST* or *DELETE*. Please not that *GET* requests do **NOT** support a data payload.
    ```yaml
     method=POST
     ```
- The `dataType` defines how the data payload is sent to the server. This can be either *JSON* or
    *FORM*. Please note that when using *FORM* the actual payload is still formatted as JSON,
    but is wrapped into a form-property called *data*
    ```yaml
    dataType=JSON
    ```
- Setting `enabled` to false disabled the hook. This is useful when the hook isn't needed anymore
    but should be kept in the config in case it might have to be re-enabled later on.
    ```yaml
    enabled=false
    ```
- Setting `details` to true includes more information in the data payload. This can be used to
    avoid a second follow-up request in case specific information about an object is required.
    ```yaml
    details=false
    ```
- The `permissions` node specifies what data is sent to the endpoint. Refer to the 
    [permissions documentation](PERMISSIONS.md) for more information regarding permissions.
    Use `permissions="*"` to send all data (also depends on `details`)
    ```yaml
    permissions="*"
    ```
- `headers` are passed to the server as HTTP header fields. This can be usefull to include some 
    kind of authentication so that the other endpoint can be sure only the Web-API is sending data.
    It can also be used for custom values when using command hooks (see below)
    ```yaml
    headers=[{
        name=X-WEBAPI-KEY
        value=MY-SUPER-SECRET-KEY
    }]
    ```
- `filter` defines if and which filter is applied to this WebHook

Here is an example of a hook:
```yaml
{
    address="http://127.0.0.1:25000"
    method=POST
    dataType=JSON
    enabled=true
    details=false
    permissions="*"
    headers=[{
        name=X-WEBAPI-KEY
        value=MY-SUPER-SECRET-KEY
    }]
    filter {
        name="WebAPI-BlockType"
        config=[
            "minecraft:wooden_button"
            "minecraft:stone_button"
        ]
    }
}
```
This hook would contact the server running at `127.0.0.1:25000` and send an HTTP POST request to it.
The request would contain an additional header called `X-WEBAPI-KEY` with the value
`MY-SUPER-SECRET-KEY`. The data would be formatted as JSON and would not include details. The hook would
only be executed if the `WebAPI-BlockType` filter would allow it - in this case when either a 
`wooden_button` or `stone_button` block are being targeted.

Let's look at another example:
```yaml
{
    address="http://webapi.mydomain.com/hook.php"
    method=POST
    dataType=FORM
    enabled=true
    details=true
    permissions="*"
}
```
This hook would contact the script at `webapi.mydomain.com/hook.php` and send an HTTP POST request 
to it. The data would be formatted as `application/x-www-form-urlencoded` and include all details.


> The Web-API supports multiple hooks, which is why all the configuration nodes where you can put a 
WebHook are lists. These hooks are all fired when that particular thing happens, but they are **NOT** 
guaranteed to be executed in any specific order, and **MIGHT** even be executed simultaneously.


<a name="types"></a>
## Hook types

There are currently 3 different kinds of hooks:

1. **[Event hooks](#event)**  
These hooks are executed for certain Minecraft events

1. **[Custom event hooks](#custom-event)**  
These hooks are executed for any custom events that you define

1. **[Command hooks](#command)**  
These hooks are executed by calling a specific command, and support arguments


<a name="event"></a>
## Event hooks

Event hooks are fired when a specific Minecraft event happens. The Web-API supports quite a lot
of events out of the box, but if you need any event that isn't listed here you can always use
a **custom event hook**, described in the next section.  

Following is a list of all the events supported by the Web-API, as well as a short explanation
as to when that event is triggered.

| Event / Hook name   | Description |
| ------------------- | ----------- |
| ALL                 | Fired for all events listed below
| ACHIEVEMENT         | Fired when a player earns a new achievement
| BLOCK_UPDATE_STATUS | Fired when a Web-API BlockUpdate changes status
| CHAT                | Fired when a chat message is sent (by players)
| COMMAND             | Fired when a command is executed
| GENERATE_CHUNK      | Fired when a new chunk is generated
| EXPLOSION           | Fired when an explosion happens
| INTERACT_BLOCK      | Fired when a player interacts with a block
| INVENTORY_OPEN      | Fired when a player opens an inventory
| INVENTORY_CLOSE     | Fired when a player closes an inventory
| PLAYER_JOIN         | Fired when a player joins the server
| PLAYER_LEAVE        | Fired when a player leaves the server
| PLAYER_DEATH        | Fired when a player dies
| PLAYER_KICK         | Fired when a player gets kicked from the server
| PLAYER_BAN          | Fired when a user is banned from the server
| SERVER_START        | Fired after the server starts
| SERVER_STOP         | Fired before the server stops
| WORLD_SAVE          | Fired when a world is saved
| WORLD_LOAD          | Fired when a world is loaded
| WORLD_UNLOAD        | Fired when a world is unloaded

To subscribe to one of these events add your WebHook to the specific list in the `events` object.
For example:
```yaml
events {
    achievement=[
        # YOUR HOOKS GO HERE
    ]
}
```


<a name="custom"></a>
## Custom event hooks

Some events are not included in the Web-API, usually because I didn't know about them, or didn't have
enough time to add them. **Events of other plugins and/or mods are also not included**, this is for
obvious reasons as there are way too many mods to support.

This is why the Web-API offers custom event hooks, where you can listen to any Sponge events you wish,
and add your hooks to those.

To subscribe to a custom event just add the fully qualified class name of the event to the `custom`
object as the key, and a list of hooks as the value.  
For example:
```yaml
custom={
    "org.spongepowered.api.event.command.SendCommandEvent": [
        # YOUR HOOKS GO HERE
    ]
}
```


<a name="command"></a>
## Command hooks

Command hooks are invoked with specific commands and can even contain arguments. This is a good way
to offer players a way to trigger certain actions.

Command hooks are defined in the `command` object in the config. Let's start with an example:
```yaml
command = {
    test={
        enabled=true
        
        aliases=[
            testing
        ]
        
        params=[
            {
                name=the_player
                type=player
            },
            {
                name=the_world
                type=world
                optional=true
            }
        ]
    
        hooks=[{
            address="http://127.0.0.1:25000/{the_world}"
            enabled=false
            headers=[{
                name=X-WEBAPI-PLAYER
                value="{the_player}"
            }]
        }]
    }
}
```
This command hook is called `test`, and is therefor invoked by calling `/webapi notify test`. 

The property `enabled` can be set to false to disable the whole command hook, including registering
the command with the command registry.

It has `testing` set up as an alias, and can therefor also be called by using `/testing`. Be careful
with aliases, as they can overwrite existing minecraft commands.

It has 2 parameters, the first one is a player, the second one is optional and is a world. You would
call the command with e.g. `/webapi notify test @p world` or `/testing Valandur`.  
> The Web-API supports the following parameter types:  
STRING, BOOL, INTEGER, DOUBLE, PLAYER, WORLD, LOCATION, VECTOR3D, VECTOR3I

Any of these parameters can be marked with `optional=true` to make it an optional parameter.

The `hooks` property is a list that contains hook definitions similar to the ones described further
above. Additionally they support the parameters defined in the `params` object.  
When using the variable name of a parameter in curly braces (e.g. `{the_player}` for the example above)
this value is replaced with the value of the parameter during execution. For worlds and players this 
is their UUID, for all other types it's the exact value.

All parameters are also automatically included in the payload of the HTTP request.


<a name="filters"></a>
## WebHook Filters
WebHook filters can be used to filter out only certain events. Read more about them
[here](WEBHOOKS_FILTERS.md).


<a name="general"></a>
## Hook responses

If the endpoint that the WebAPI contacts returns any other HTTP Code than 200 it will
be logged to the console.

The endpoint may return a JSON object to instruct the Web-API to send messages to players. This can
be used to give players a feedback in response to their actions (useful for command hooks) or inform
admins of certain events.

The structure of the JSON response should look as follows:
```json
{
    "targets": [ "357427c6-3b91-4ead-aad4-15a3e18e6452", "server" ],
    "formatting": "CODE",
    "message": "Hello there!"
}
```
The `targets` property is an array of strings. These can either be player UUIDs, to which the 
message is sent directly, or the string `"server"`, which means the message will be broadcast to 
the whole server.

The `message` property is the actual message that is sent to the players/server. It is formatted with 
[Ampersand formatting](https://docs.spongepowered.org/stable/en-GB/plugin/text/representations/formatting-code-legacy.html#ampersand-formatting)
