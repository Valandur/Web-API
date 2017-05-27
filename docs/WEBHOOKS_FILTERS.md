# Web-API WebHook Filters

WebHook filters are used to filter out events that you don't want to be informed about.
This is especially useful with events that are triggered very often, such as the INTERACT_BLOCK event.

## Guidelines
> All filters must extend `valandur.webapi.hook.WebAPIFilter`

> Filters must be in the `/webapi/filters` folder (**NOT** the config folder).

> The package name must match the folder structure. If filters are placed in the 
`webapi/filters` folder then they must be in the `filters` package.

> The filename must match the class name, otherwise the Web-API won't find it.

## Basics
The following filter would filter out all events that are not a button press on a button.

```java
package filters;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.block.InteractBlockEvent;
import valandur.webapi.hook.WebAPIFilter;
import valandur.webapi.hook.WebHook;

public class BlockUpdateFilter extends WebAPIFilter {
    @Override
    public String getName() {
        return "BlockUpdate-ButtonOnly";
    }

    @Override
    public boolean process(WebHook hook, Object data) {
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
    filter="BlockUpdate-ButtonOnly"
}
```

## Filter class methods
Your custom filter needs to override two methods from the abstract `WebAPIFilter` class.

1. `public String getName()` must return a `String` which is the name that is used in the `hooks.conf` 
file to active the filter for a certain WebHook

1. `public boolean process(WebHook hook, Object data)` is the actual processing function of the filter
and gets passed two arguments:
    1. `WebHook hook` is the hook this filter is currently being executed for (this may change if the
    filter is applied to multiple hooks)
    1. `Object data` is the data that will be serialized and sent to the server. This is usually an event.
    
    The function must return `true` if the data should be sent to the hook, or `false` if it should
    not be further processed.