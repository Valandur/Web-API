# Web-API Additional Data
The Web-API provides additional data which is not documented on the endpoints because 
it may or may not be present.

The following documentation tries to list all these properties, they will be present 
on objects for which it makes sense. The current implementation state of the various data
can be found [here](DATA_IMPL.md).

Objects from the `/player`, `/entity`, `/tile-entity` as well as any item stacks 
(in their `data` properties) may contain none, one or multiple of these properties.

> In the documentation below the "carrier" of a property is often referred to as the 
'object', this is done to emphasize the fact that the property can be attached to 
possibly various different things.


## Table of Contents
1. [Achievements](#achievements)
1. [Age](#age)
1. [Career](#career)
1. [Durability](#durability)
1. [Dye](#dye)
1. [Experience](#experience)
1. [Food](#food)
1. [GameMode](#gameMode)
1. [Health](#health)
1. [Joined](#joined)
1. [PotionEffects](#potionEffects)
1. [Sheared](#sheared)
1. [Sign](#sign)
1. [Spawn](#spawn)
1. [Statistics](#statistics)
1. [Tameable](#tameable)
1. [Trades](#trades)

<a name="achievements"></a>
## Achievements
Contains a list of achievements that have been granted to the object.
```json
"achievements": [
    {
      "id": "mine_wood",
      "name": "Getting Wood",
      "class": "net.minecraft.stats.Achievement",
      "description": "Attack a tree until a block of wood pops out"
    }
]
```

<a name="age"></a>
## Age
Contains information about the age of an object.
```json
"age": {
  "adult": false,
  "age": 35,
}
```

<a name="career"></a>
## Career
Describes the profession of the object.
```json
"career": "minecraft:priest"
```


<a name="durability"></a>
## Durability
Describes the durability of an object.
```json
"durability": {
    "unbreakable": false,
    "durability": 1561
}
```


<a name="dye"></a>
## Dye
The color of an object.
```json
"dye": "white"
```


<a name="experience"></a>
## Experience
The amount of experience an object has.
```json
"experience": {
    "level": 0,
    "experience": 0,
    "totalExperience": 0
}
```


<a name="food"></a>
## Food
The current food levels of an object.
```json
"food": {
    "foodLevel": 20,
    "exhaustion": 3.049999952316284,
    "saturation": 1.0
}
```


<a name="game-mode"></a>
## GameMode
The game mode the object is currently in.
```json
"gameMode": "minecraft:creative"
```


<a name="health"></a>
## Health
The health information of the object
```json
"health": {
    "current": 20.0,
    "max": 20.0
}
```


<a name="joined"></a>
## Joined
Information about when the object first/last joined the server.
```json
"joined": {
    "first": 1489329688,
    "last": 1492426023
}
```


<a name="potion-effects"></a>
## PotionEffects
A list of effects from potions attached to/affecting the object.
```json
"potionEffects": [{
    "type": "minecraft:instant_damage",
    "amplifier": 1,
    "duration": 1
}]
```


<a name="sheared"></a>
## Sheared
True if the object has been sheared, false otherwise.
```json
"sheared": false
```


<a name="sign"></a>
# Sign
A list of lines of text that are displayed on an object.
```json
"sign": [
    "Hi",
    "How are you?",
    "",
    "Web-API"
 ]
```


<a name="spawn"></a>
## Spawn
Information about which entity is spawned by the object.
```json
"spawn": "minecraft:skeleton_horse"
```


<a name="statistics"></a>
## Statistics
Statistical data about the object.
```json
"statistics": {
    "chest_opened": 2,
    "use_item.minecraft.dispenser": 1,
    "walk_one_cm": 38715,
    "use_item.minecraft.sapling": 1,
    "use_item.minecraft.chest": 2,
    "craft_item.minecraft.planks": 16,
    "leave_game": 34,
    "craft_item.minecraft.crafting_table": 1,
    "dispenser_inspected": 1,
    "use_item.minecraft.dark_oak_stairs": 2,
    "use_item.minecraft.dye": 2,
    "mine_block.minecraft.log": 1,
    "talked_to_villager": 6,
    "pickup.minecraft.log": 62,
    "fly_one_cm": 1351,
    "deaths": 10,
    "jump": 15,
    "play_one_minute": 1107890,
    "fall_one_cm": 20680,
    "use_item.minecraft.planks": 2,
    "sneak_time": 5,
    "use_item.minecraft.spruce_stairs": 5,
    "crouch_one_cm": 31,
    "time_since_death": 485335,
    "pickup.minecraft.sapling": 1,
    "use_item.minecraft.dirt": 11,
    "sprint_one_cm": 1745,
    "use_item.minecraft.stained_glass": 1,
    "use_item.minecraft.sign": 3,
    "use_item.minecraft.red_sandstone_stairs": 2,
    "use_item.minecraft.spawn_egg": 8
}
```


<a name="tameable"></a>
## Tameable
Signifies that an object is tameable and contains information about whether the 
object is tamed or not and the possible owner.
```json
"tameable": {
    "isTamed": true,
    "owner": {
        "type": "minecraft:player",
        "uuid": "357427c6-3b91-4ead-aad4-15a3e18e6452",
        "location": {
            "world": {
                "name": "world",
                "uuid": "c574ab53-831b-4410-b3ad-955a2a504843",
                "link": "/api/world/c574ab53-831b-4410-b3ad-955a2a504843"
            },
            "position": {
                "x": -2.0039034033218073,
                "y": 4.0,
                "z": -2.026152060791321
            }
        },
        "name": "Valandur",
        "link": "/api/player/357427c6-3b91-4ead-aad4-15a3e18e6452",
        "class": "net.minecraft.entity.player.EntityPlayerMP"
    }
}
```


<a name="trades"></a>
## Trades
Contains information about the trades the object offers.
```json
"trades": [{
    "hasExpired": false,
    "grantsExp": true,
    "uses": 0,
    "maxUses": 0,
    "firstBuyingItem": {
        "id": "minecraft:rotten_flesh",
        "quantity": 39
    },
    "secondBuyingItem": {
        "id": "minecraft:air",
        "quantity": 0
    },
    "sellingItem": {
        "id": "minecraft:emerald",
        "quantity": 1
    }
}]
```