# Web-API WebHook Filters
WebHook filters are used to filter out events that you don't want to be informed about.
This is especially useful with events that are triggered very often, such as the INTERACT_BLOCK event.

If you need a filter which isn't present feel free to contact me or leave a pull request.

## Table of Contents
1. [WebAPI-BlockType](#builtin-blocktype)
1. [WebAPI-Player](#builtin-player)
1. [WebAPI-Item](#builtin-item)


<a name="builtin"></a>
The Web-API comes with a few built-in filters that can be used directly in the config file.  
Currently the following filters are provided:


<a name="builtin-blocktype"></a>
## WebAPI-BlockType
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


<a name="builtin-player"></a>
## WebAPI-Player
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


<a name="builtin-item"></a>
## WebAPI-Item
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
