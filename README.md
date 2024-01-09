# WEB-API

> This is a major rework of the original Web-API Project,
> geared towards support across multiple Minecraft mod / plugin systems
> and GraphQL support!

## Introduction

This plugin provides a RESTful web API to manage a Minecraft server. This can be used to perform various administrative
tasks through the web, or provide dynamic content for users.

## Endpoints

### Players

| Description                          | Fabric | Forge | Spigot | Sponge |
|--------------------------------------|--------|-------|--------|--------|
| List players                         | ✓      | ✓     | ✓      | ✓      |
| Get player details                   | ✓      | ✓     | ✓      | ✓      |
| Get player inventory                 | ✓      | ✓     | ✓      | ✓      |
| Add items to player inventory        | ✓      | ✓     | ✓      | ✓      |
| Remove items from player inventory   | ✓      | ✓     | ✓      | ✓      |
| Get player ender chest               | ✓      | ✓     | ✓      | ✓      |
| Add items to player ender chest      | ✓      | ✓     | ✓      | ✓      |
| Remove items from player ender chest | ✓      | ✓     | ✓      | ✓      |

### Worlds

| Endpoint      | Fabric | Forge | Spigot | Sponge |
|---------------|--------|-------|--------|--------|
| List worlds   | ✓      | ✓     | ✓      | ✓      |
| World details | ✓      | ✓     | ✓      | ✓      |
| Load world    | ✓      | ✓     | ✓      | ✓      |
| Unload world  | ✓      | ✓     | ✓      | ✓      |
| Create world  | ✓      | ✓     | ✓      | ✓      |
| Delete world  | ✓      | ✓     | ✓      | ✓      |
| Get block     | ✓      | ✓     | ✓      | ✓      |
| Set block     | ✓      | ✓     | ✓      | ✓      |

### Server

| Endpoint | Fabric | Forge | Spigot | Sponge |
|----------|--------|-------|--------|--------|
| Info     | ✓      | ✓     | ✓      | ✓      |
| Stats    | ✓      | ✓     | ✓      | ✓      |

## Contributors & Special thanks

- [johnfg10](https://github.com/johnfg10)
- [r15ch13](https://github.com/r15ch13)
- [Cat121](https://github.com/Cat121)
- [Sherex](https://github.com/Sherex)
- [kawahara](https://github.com/kawahara)
- [Lantcoder](https://github.com/Lantcoder)
- [crymates](https://github.com/crymates)
- [DosMike](https://github.com/DosMike)
