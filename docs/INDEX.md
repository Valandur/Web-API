# Web-API documentation & tutorials

This is a collection of various documentations and tutorials for various sections of the Web-API.

1. [Consumer Tutorial](CONSUME.md)  
This tutorial shows some basic examples of how to use/consume the Web-API. This will be helpfull
if you're building an app that relies on the data from the Web-API

1. [General config](CONFIG.md)  
This sections describes the basic configuration options of the Web-API.

1. [Permissions](PERMISSIONS.md)  
This documentation talks about the `permissions.conf` file and the various settings you can use
to adjust the Web-API to your needs, specifically the permissions that restrict access and data.

1. [WebHooks](WEBHOOKS.md)  
This documentation explains how to use WebHooks to have your own app stay informed about stuff
that happens on the minecraft server, without constantly requesting data.

1. [WebHook Filters](WEBHOOKS_FILTERS.md)  
WebHook filters can be used to filter out certain web hook events and greatly reduce the amount
of events that are sent to your server. Read the [WebHook documentation](WEBHOOKS.md) first.

1. [Additional Data](DATA.md)  
This documentation talks about additional data that is included with player, world and entity
endpoints, as well as the implementation progress of further data which is not yet supported.

1. [Serializers](SERIALIZERS.md)  
This short manual shows how to start writing custom serializers for the Web-API, to make sure
that any custom data is turned properly into JSON. Specifically useful for other plugins/mods.
