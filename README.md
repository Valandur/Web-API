# Web-API
A RESTful web API for Minecraft Sponge

## About
This plugin provides a RESTful web API to manage a Sponge server.
This can be used to perform various administrative tasks through the web, or provide dynamic content for users.

## Installation
1. Setup a Sponge server (Sponge Vanilla & SpongeForge supported)
2. Add this mod to the mods folder
3. Start the server
4. Configure the config files to your needs.

## Tutorial
To learn more about how to interact with the API, go [here](docs/TUTORIAL.md)

## Security
The default settings run the server **on localhost only**, which means you cannot access it from another server.  
Usually this is enough, as you most likely will be running a web server / another application that accesses this data on the same server.  

**PLEASE BE CAREFUL WHO YOU GRANT ACCESS TO THE WEB API, AS PEOPLE WITH ACCESS TO THE COMMAND ENDPOINT CAN EXECUTE ARBITRARY COMMANDS
ON THE MINECRAFT SERVER, INCLUDING SHUTTING IT DOWN AND DELETING WORLDS!**

There is a simple permissions system in place to restrict access to the API. Check out the webapi/permissions.conf file in the config folder.  
**REMEMBER TO CHANGE THE DEFAULT KEYS THAT ARE PROVIDED IN THE CONFIG FILE**  
You have to provide a "key" if you want to access any api routes. There are two ways to pass it to the server.
* Set the "x-webapi-key" header in the request
* Set the "key" query parameter in the request

## Request / Response types
This API uses JSON as requeset and response formats.

## Routes
When accessing the base site (by default http://localhost:8080/) a documentation of all the methods will show up.
This can be used as a starting point to explore the supported methods. You can also find the same documentation at
https://valandur.github.io/Web-API/redoc.html

| Path         | Description                                                           |
|--------------|-----------------------------------------------------------------------|
| /            | Base API url, shows a list of all supported paths                     |
| /info        | Provides basic information about the minecraft server                 |
| /history     | Provides access to the chat and command history                       |
| /cmd         | Lists all commands & executes individual commands                     |
| /player      | Lists all players & information about individual players              |
| /world       | Lists all worlds & information about individual worlds                |
| /plugin      | Lists all plugins & information about individual plugins              |
| /entity      | Lists all entities & information about individual entities            |
| /tile-entity | Lists all tile entities & information about individual tile entities  |
| /class       | Provides information about loaded classes on the server               |
