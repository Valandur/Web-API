# Web-API Permissions
The extensive permissions system allows you to configure which data can be accessed by whom.

The main permissions file is located in the `config/webapi` folder and called `permissions.conf`.
Some additional permissions related to hooks are located in the `hooks.conf` file. This documentation
focuses mostly on the `permissions.conf` file, but the most of the things also apply to the 
`hooks.conf` file.

Most of the `permissions.conf` file contains comments to explain what each part of the config does.


## Table of Contents
1. [Whitelist](#whitelist)
1. [Blacklist](#blacklist)
1. [Default permissions (key-less clients)](#default-perms)
1. [Permissions with keys](#key-perms)
1. [PermissionsTree](#permissions-tree)


<a name="whitelist"></a>
# Whitelist
```yaml
# Set this to true to enable the whitelist, false to turn it off
useWhitelist = true

# Add IP address that are allowed to connect to the Web-API to this list
whitelist = [
    "127.0.0.1"
]
```

The whitelist defines a basic IP-based restriction on who may access the Web-API. This setting
is enabled by default, and set only to allow the localhost to connect.

If you wish to access the Web-API from another server than the one that your minecraft server is
running on, you will have to add that IP here, or turn off whitelisting.

> Turning off the whitelist is not recommended, unless you have properly set up permissions


<a name="blacklist"></a>
# Blacklist
```yaml
# Set this to true to enable the blacklist, false to turn it off
useBlacklist = false

# Add the IP addresses that are NOT allowed to connect to the Web-API to this list
blacklist = [
    "0.0.0.0"
]
```

The blacklist controls which hosts are **NOT** allowed to access the Web-API. Use this to block
out possible hosts spamming your server with requests.


<a name="default-perms"></a>
# Default permissions (key-less clients)
```yaml
# These are the default permissions that a client without a key receives
default {
    # The permissions define which endpoints a user with this key can access, and what data is
    # returned for each endpoint
    permissions = {
        info = "*"
        player = {
            get = {
                "*" = true
                uuid = false
            }
        }
    }

    # The rate limit specifies how many operations per second a client with this key can execute
    rateLimit = 10
}
```

These permissions are the ones that a client which doesn't specify a key receives. These
permissions should generally be really restrictive, as anyone who can access the Web-API
can access this data.

The `rateLimit` node specifies how many operations per second a client can execute.

The `permissions` node is a PermissionsTree, which is explained down below and specifies which
endpoints and what data from those endpoints a client can access.


<a name="key-perms"></a>
# Permissions with keys
```yaml
# This is an array of keys, defining which keys give access to which endpoints.
keys = [{
    key = ADMIN

    # The "*" stands for all permissions and data
    permissions="*"

    # No rate limit or zero = unlimited requests
    rateLimit = 0
}]
```

The `keys` array is an array of permission structures similar to the ones explained above. The
only difference is that they have a `key` attribute, which is the key that the client needs
to pass in order to gain access to this set of permissions.

> The key should generally be something secure and long enough to make it unfeasable to guess.
The suggested way to go about this is to generate an arbitrary hash or password of at least
16 characters length

The client then has two ways to pass this key when accessing the Web-API:
* Set the `x-webapi-key` **header** in the request
* Set the `key` **query parameter** in the request


<a name="permissions-tree"></a>
# PermissionsTree
The `permissions` properties above as well as the `permissions` properties listed in the
`hooks.conf` file use what is here referred to as a `PermissionsTree`. The tree defines
which endpoints of the API can be accessed and what data is returned for those endpoints.

```yaml
permissions = {
    info = "*"
    player = {
        get = {
            "*" = true
            uuid = false
        }
    }
}
```

The two first levels of the permissions tree refer to the method of the Web-API (except when
using a `PermissionsTree` for the `hooks.conf` file. In that case you must leave those away).

So in this case `info` refers to the *Info* endpoint, which provides general information about
the minecraft server. Since this node is set to `*`, which is the *allow all* permission,
all the data for that endpoint will be returned.

The `player` node refers to the *Player* endpoint, which provides information about players
(when sending `GET` requests), and allows executing methods on the player object (when sending
`POST` requests).

Since the `player.post` permission node is not set, it is not allowed, and sending `POST`
requests to this endpoint will yield a `403 - Not allowed` error.

The `player.get` permission is allowed, meaning that accessing the endpoint with a `GET`
request will return data. Which data is specified through the `get` node. The `*` node means
that all data is allowed, but more specific permissions always override the `*` node. So
in this case, all data *except* the `uuid` will be returned.


## Key
The key is a string specifying the name of the node. Following keys have special meaning:

|  Key  | Description                             |
|:-----:| --------------------------------------- |
|   .   | Refers to this permission               |
|   *   | Referes to this and all sub permissions |


## Value
Each node can have any one of following values:
- `true`
- `false`
- `*`
- An object mapping keys to permission nodes


## Examples

### Allow
```yaml
info = true
```

Key: `info`  
Value: `true`  

Allows access to the `info` node. Does **not** allow access to any sub nodes.


### Allow all
```yaml
info = "*"
```

Key: `info`  
Value: `*`  

This node allows full access to the `info` node and all sub nodes.


### Deny
```yaml
info = false
```

Key: `info`  
Value: `false`  

Denies access to the `info` node and all sub nodes.


### Mixed
```yaml
player {
  get {
    "*" = true
    uuid = false
    location {
      y = false
    }
  }
  post {
    "." = false
  }
}
```

Key: `player`  
Value: `An object mapping keys to permission nodes`  

Allows full access to the `player.get` path, **except** for the `uuid` path and 
the `location.y` path.  
Denies access to the `player.post` path. This could also be written as: `post = false`
