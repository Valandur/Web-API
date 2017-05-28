# Web-API WebHook Filters
WebHook filters are used to filter out events that you don't want to be informed about.
This is especially useful with events that are triggered very often, such as the INTERACT_BLOCK event.

## Built-In filters
The Web-API comes with a few built-in filters that can be used directly in the config file.  
Currently the following filters are provided:

### WebAPI-BlockType
This filter only forwards events that implement TargetBlockEvent and the target block has a certain
block type. The block type(s) can be configured in the options.
```yaml
filter {
    name="WebAPI-BlockType"
    config=[
        "minecraft:stone_button"
        "minecraft:wooden_button"
    ]
}
```

### WebAPI-Player
This filter only forwards events that implement TargetPlayerEvent and target a specific player (either
specified by UUID or name).
```yaml
filter {
    name="WebAPI-Player"
    config=[
        "Valandur"
        "357427c6-3b91-4ead-aad4-15a3e18e6452"
    ]
}
```

### WebAPI-Item
This filter only forwards events that implement TargetItemEvent and target a specific type of item
which can be configured in the options.
```yaml
filter {
    name="WebAPI-Item"
    config=[
        "minecraft:stone_sword"
    ]
}
```

## Custom filters
> All filters must extend `valandur.webapi.hook.WebHookFilter`

> Filters must be in the `/webapi/filters` folder (**NOT** the config folder).

> The package name must match the folder structure. If filters are placed in the 
`webapi/filters` folder then they must be in the `filters` package.

> The filename must match the class name, otherwise the Web-API won't find it.

The following filter would filter out all events that are not a button press on a button.

```java
package filters;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.block.InteractBlockEvent;
import valandur.webapi.hook.WebHook;
import valandur.webapi.hook.WebHookFilter;
import valandur.webapi.WebAPI;

public class BlockUpdateFilter extends WebHookFilter {

    public static String name = "BlockUpdate-ButtonOnly";

    public BlockUpdateFilter(WebHook hook, ConfigurationNode config) {
        super(hook, config);
    }

    @Override
    public boolean process(Object data) {
        if (data instanceof InteractBlockEvent) {
            InteractBlockEvent event = (InteractBlockEvent)data;
            return event.getTargetBlock().getState().getType() == BlockTypes.STONE_BUTTON ||
                    event.getTargetBlock().getState().getType() == BlockTypes.WOODEN_BUTTON;
        }

        return true;
    }
}
```

With the following hook configuration the hook would only send button presses to the specified address

```yaml
{
    address="http://localhost:25000"
    method=POST
    dataType=JSON
    enabled=true
    details=true
    permissions="*"
    filter {
        name="BlockUpdate-ButtonOnly"
    }
}
```

## Filter class methods
Your custom filter needs to override one method from the `WebHookFilter` class and provide 
a static property.

1. `public static String name` is the name that is used in the `hooks.conf` file to active the filter 
for a certain WebHook

1. `public boolean process(Object data)` is the actual processing function of the filter
    1. The hook that this filter is currently being executed for can be accessed with `this.hook`.
    1. `Object data` is the data that will be serialized and sent to the server. This is usually an event.
    
    The function must return `true` if the data should be sent to the hook, or `false` if it should
    not be further processed.