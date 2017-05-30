# Web-API Tutorial
A few simple examples on how to consume the Web-API.

Remember that if you are accessing the Web-API without a key then you are using the default
permissions, and might not be able to access all the data. To pass a key proceed as follows:
* Set the `x-webapi-key` **header** in the request
* Set the `key` **query parameter** in the request


## Table of Contents
1. [Setup](#setup)
1. [Server Info](#server-info)
1. [World Info](#world-info)
1. [Commands](#commands)


<a name="setup"></a>
## Setup
We're going to be using NodeJS for these few simple tutorials, but of course you can work with 
whatever language you prefer.

1. Install [NodeJS](https://nodejs.org/en/download/)

1. Create a new directory in which your project is going to reside

1. Run `npm init` inside that folder to setup a new project.

1. Run `npm i request lodash --save` to install the request module, which we'll be using to comunicate
with the Web-API, and the Lodash module, which offers lots of functions to work with lists and objects.


<a name="server-info"></a>
## Server Info

### Step-by-step
1. Import the request module, which we'll use to contact the Web-API server
```javascript
const req = require("request");
```

2. Save the base route to our Web-API server (change this if your Minecraft/Web-API 
server is running on another server)
```javascript
const BASE_API_URL = "http://localhost:8080/api";
```

3. Create a callback function which will get executed when the Web-API returns a response (or the 
request times out or errors). Log any errors, the status code and the response (body) to the console.
```javascript
const callback = (err, response, body) => {
    console.log(err);
    console.log(response.statusCode);
    console.log(JSON.stringify(body, null, 2));
};
```

4. Send a `GET` request to the `BASE_API_URL` url, and parse the response as a JSON object.
```javascript
req.get({ url: BASE_API_URL + "/info", json: true }, callback);
```


### Code
info.js
```javascript
const req = require("request");

const BASE_API_URL = "http://localhost:8080/api";

const callback = (err, response, body) => {
    console.log(err);
    console.log(response.statusCode);
    console.log(JSON.stringify(body, null, 2));
};

req.get({ url: BASE_API_URL + "/info", json: true }, callback);
```

### Results
Run the script with `node info.js`

Something similar to this will be logged in the console (on a vanilla server):
```
null
200
{
  "motd": "A Minecraft Server",
  "players": 1,
  "maxPlayers": 20,
  "uptimeTicks": 85404,
  "hasWhitelist": false,
  "minecraftVersion": "1.11.2",
  "game": {
    "id": "minecraft",
    "name": "Minecraft",
    "version": "1.11.2",
    "description": "Minecraft is a game about placing blocks and going on adventures",
    "url": "1.11.2",
    "authors": ["Mojang"]
  },
  "api": {
    "id": "spongeapi",
    "name": "SpongeAPI",
    "version": "6.0.0-SNAPSHOT-8aabd12",
    "description": "A Minecraft plugin API",
    "url": "6.0.0-SNAPSHOT-8aabd12",
    "authors": []
  },
  "implementation": {
    "id": "sponge",
    "name": "SpongeVanilla",
    "version": "1.11.2-6.0.0-BETA-229",
    "description": "The SpongeAPI implementation for Vanilla Minecraft",
    "url": "1.11.2-6.0.0-BETA-229",
    "authors": []
  }
}
```


<a name="world-info"></a>
## World Info

### Code
world-info.js
```javascript
const req = require("request");
const _   = require("lodash");

const BASE_API_URL = "http://localhost:8080/api";

const HEADERS = {
    "x-webapi-key": "ADMIN",
};

req.get({ url: BASE_API_URL + "/world", json: true, headers: HEADERS }, (err, res, body) => {
    console.log("--- WORLDS ---");
    console.log(err);
    console.log(res.statusCode);
    console.log(JSON.stringify(body, null, 2));
    
    const world = _.first(body.worlds);
    if (!world) {
        console.log("No worlds!");
        return;
    }
    
    req.get({ url: BASE_API_URL + "/world/" + world.uuid, json: true, headers: HEADERS }, (err, res, body) => {
        console.log("--- WORLD " + world.name + " ---");
        console.log(err);
        console.log(res.statusCode);
        console.log(JSON.stringify(body, null, 2));
    });
});
```

### Results

Run the script with `node world-info.js`

Something similar to this will be logged in the console (on a vanilla server):
```
--- WORLDS ---
null
200
{
  "worlds": [
    {
      "name": "DIM1",
      "uuid": "1032d0cc-f1cb-45d1-a164-6cc0e820af90"
    },
    {
      "name": "world",
      "uuid": "19cce8ff-45c7-4546-9abf-d92fc8394f9f"
    },
    {
      "name": "DIM-1",
      "uuid": "092b1865-ed31-4a19-881f-e0deeb2d8077"
    }
  ]
}
--- WORLD DIM1 ---
null
200
{
  "world": {
    "name": "DIM1",
    "uuid": "1032d0cc-f1cb-45d1-a164-6cc0e820af90",
    "data": {
      "randomSeed": 2136025272722747600,
      "generatorName": "default",
      "borderCenterZ": 0,
      "difficulty": 1,
      "borderSizeLerpTime": 0,
      "raining": 0,
      "dimensionData": {
        "1": {
          "DragonFight": {
            "Gateways": [
              2,
              10,
              12,
              5,
              15,
              3,
              16,
              4,
              0,
              8,
              11,
              13,
              9,
              1,
              19,
              17,
              6,
              7,
              14,
              18
            ],
            "DragonKilled": 1,
            "PreviouslyKilled": 1
          }
        }
      },
      "time": 142330,
      "gameType": 0,
      "mapFeatures": 1,
      "borderCenterX": 0,
      "borderDamagePerBlock": 0.2,
      "borderWarningBlocks": 5,
      "borderSizeLerpTarget": 60000000,
      "version": 19133,
      "dayTime": 142330,
      "initialized": 1,
      "allowCommands": 0,
      "sizeOnDisk": 0,
      "gameRules": {
        "doTileDrops": "true",
        "doFireTick": "true",
        "reducedDebugInfo": "false",
        "naturalRegeneration": "true",
        "disableElytraMovementCheck": "false",
        "doMobLoot": "true",
        "keepInventory": "false",
        "doEntityDrops": "true",
        "mobGriefing": "true",
        "randomTickSpeed": "3",
        "commandBlockOutput": "true",
        "spawnRadius": "10",
        "doMobSpawning": "true",
        "maxEntityCramming": "24",
        "logAdminCommands": "true",
        "spectatorsGenerateChunks": "true",
        "doWeatherCycle": "true",
        "sendCommandFeedback": "true",
        "doDaylightCycle": "true",
        "showDeathMessages": "true"
      },
      "spawnY": 50,
      "rainTime": 0,
      "thunderTime": 0,
      "spawnZ": 0,
      "hardcore": 0,
      "difficultyLocked": 0,
      "spawnX": 0,
      "clearWeatherTime": 0,
      "thundering": 0,
      "generatorVersion": 1,
      "borderSafeZone": 5,
      "generatorOptions": "",
      "lastPlayed": 1488641073905,
      "borderWarningTime": 15,
      "levelName": "DIM1",
      "borderSize": 60000000,
      "dataVersion": 922
    }
  }
}
```


<a name="commands"></a>
## Commands

### Code
commands.js
```javascript
const request = require("request");
const _ = require("lodash");

const BASE_API_URL = "http://localhost:8080/api";
const HEADERS = {
    "x-webapi-key": "ADMIN",
};

const BODY = {
    "name": "Valandur",
    "command": "list",
    "waitTime": 1,
};

request.post({ url: BASE_API_URL + "/cmd", json: true, headers: HEADERS, body: BODY }, (err, response, body) => {
    console.log(err);
    console.log(response.statusCode);
    console.log(JSON.stringify(body, null, 2));
});
```

### Results
```

null
200
{
  "response": [
    "There are 0/20 players online."
  ]
}
```
