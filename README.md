# Web-API
A RESTful web API for Minecraft Sponge

## About
This plugin provides a RESTful web API to manage a Sponge server.
This can be used to perform various administrative tasks through the web, or provide dynamic content for users.

## Installation
1. Setup a Sponge server (Sponge Vanilla & SpongeForge supported)
2. Add this mod to the mods folder
3. Configure webapi.conf to your needs

## Access
The default settings run the server **on localhost only**, which means you cannot access it from another server.

**THIS IS FOR SECURITY REASONS. PLEASE BE CAREFUL WHO YOU GRANT ACCESS TO THE WEB API, AS PEOPLE WITH ACCESS CAN EXECUTE ARBITRARY COMMANDS
ON THE MINECRAFT SERVER, INCLUDING SHUTTING IT DOWN AND DELETING WORLDS!**

Usually this is enough, as you most likely will be running a web server / another application that accesses this data on the same server.

## Request / Response types
This API uses JSON as requeset and response formats.

## URLs
When accessing the base site (by default http://localhost:8080/) a documentation of all the methods will show up. [WIP]
This can be used as a starting point to explore the supported methods.

| Path        | Description                                              |
|-------------|----------------------------------------------------------|
| /           | Base API url, shows a list of all supported paths        |
| /info       | Provides basic information about the minecraft server    |
| /cmd        | Lists all commands & executes individual commands        |
| /players    | Lists all players & information about individual players |
| /worlds     | Lists all worlds & information about individual worlds   |
| /chat       | Provides access to the chat history                      |
| /plugins    | Lists all plugins & information about individual plugins |
