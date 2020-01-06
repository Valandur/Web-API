# Web-API
A RESTful web API & admin panel for Minecraft Sponge

## About
This plugin provides a RESTful web API to manage a Sponge server.
This can be used to perform various administrative tasks through the web, or provide dynamic content for users.

It also adds an admin panel with which a minecraft server can easily be managed,
including running commands, chatting, managing players, destroying entities, a map
& much more.

## Features
Provides an AdminPanel to manage your minecraft server and a RESTful webserver with:

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
* An API for other plugins to expose their own endpoints easily

## Installation
1. Setup a Sponge server (Sponge Vanilla & SpongeForge supported)
2. Add this mod to the mods folder
3. Start the server
4. Configure the config files to your needs.
5. Use `sponge plugins reload` to import your config changes to the server without restarting.
(see the docs below for more information)

## Tutorials & Documentation
Go [here](docs/INDEX.md) to see a list of various documentations and tutorials

To use the Web-API, you will most likely be writing some form of client or server that
interacts with it. Client libraries for various languages are available at:
- [C#](https://github.com/Valandur/webapi-client-csharp)
- [Java](https://github.com/Valandur/webapi-client-java)
- [PHP](https://github.com/Valandur/webapi-client-php)
- [Python](https://github.com/Valandur/webapi-client-python) 
- [Javascript/Typescript](https://github.com/Valandur/webapi-client-typescript) 

## Request / Response types
The Web-API supports JSON and XML data formats for requests and responses.
Specify the `Content-Type` header to explain what content type you are sending, and
use the `accept` header to tell the Web-API what content type to return to you.
> Yes, you can send XML and get JSON in response, or the other way around if you want.

## Routes
When accessing the docs site (by default http://localhost:8080/docs) a documentation of 
all the methods will show up. This can be used as a starting point to explore the 
supported routes. You can also find the same documentation [here](https://valandur.github.io/Web-API/redoc.html).

## Contributors & Special thanks
- [johnfg10](https://github.com/johnfg10)
- [r15ch13](https://github.com/r15ch13)
- [Oblx](https://github.com/oblx)
- [Sherex](https://github.com/Sherex)
- [kawahara](https://github.com/kawahara)
- [Lantcoder](https://github.com/Lantcoder)
- [crymates](https://github.com/crymates)
- [DosMike](https://github.com/DosMike)
