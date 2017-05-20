# Web-API
A RESTful web API for Minecraft Sponge

## About
This plugin provides a RESTful web API to manage a Sponge server.
This can be used to perform various administrative tasks through the web, or provide dynamic content for users.

## Features
Provides a RESTful webserver with:

* General information about the server
* History for
  * Chat
  * Commands
* WebHooks that execute
  * on minecraft events (like players joining, chat, death, etc.)
  * when calling certain command (supports parameters)
  * on custom events (e.g. from other plugins)
* List, get details about and manipulate
  * Players
  * Worlds
  * Entities
  * Tile entities
  * Plugins
  * Blocks
* Information about loaded classes
* Arbitrary command execution (like using the server console)
* Custom serializers to determine how data is turned into JSON
* Permissions for each endpoint

## Installation
1. Setup a Sponge server (Sponge Vanilla & SpongeForge supported)
2. Add this mod to the mods folder
3. Start the server
4. Configure the config files to your needs.

## Tutorials & Documentation
Go [here](docs/INDEX.md) to see a list of various documentations and tutorials

## Request / Response types
This API uses JSON as requeset and response formats.

## Routes
When accessing the base site (by default http://localhost:8080/) a documentation of all the methods will show up.
This can be used as a starting point to explore the supported routes. You can also find the same documentation
[here](https://valandur.github.io/Web-API/redoc.html).

## Contributors

- [johnfg10](https://github.com/johnfg10)
- [r15ch13](https://github.com/r15ch13)
