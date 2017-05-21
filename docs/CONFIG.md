# Web-API configuration
The main configuration file for the Web-API is located in `config/webapi/config.conf`.
This configuration file contains general settings for the Web-API.

All of the config files in the `config/webapi` folder are commented to help in adjusting values.

> All config files contain a `version` property. This is only for the Web-API to aid in
updating your config when new values are added. You should never have to change this value.

The default config looks something like this:

```yaml
# Set this to true when working on the WebAPI. This is NOT
# detailed debug info, so don't turn on if not running the
# Web-API from an IDE
devMode = false

# This is the host the API will be listening on.
# Default is "localhost" to prevent any access from outside
# the server, but you can set this to "0.0.0.0" to listen
# on all addresses IF YOU HAVE CONFIGURED THE PERMISSIONS
host = "localhost"

# This tells the API on which port to listen for requests.
# It is recommended to use a port above 1024, as those below
# are reserved for the system and might not be available.
port = 8080

# The default amount of time, in milliseconds, that a command
# execution waits for response messages.
# THIS ONLY APPLIES WHEN THE REQUEST HAS: waitLines > 0
cmdWaitTime = 10000

# This controls the maximum amount of blocks a client can get
# in one Web-API call
maxBlockGetSize = 1000000

# This is the maximum amount of blocks that a client can change
# in one Web-API call. Please note that not all blocks are changed
# at once (see below)
maxBlockUpdateSize = 1000000

# The maximum number of blocks that are changed per second during
# a block update (related to the setting above)
maxBlocksPerSecond = 10000
```

The most noteable settings are:
- `host`  
This is the hostname that the Web-API binds to when starting. Using `localhost` or the loopback
IP `127.0.0.1` (or `::1` for IPv6) means that the Web-API server will only listen for incoming
connections on the local machine. If you need access from another machine either set this to
`0.0.0.0`, or use a proxy such as [nginx](https://nginx.org/en/) to forward requests.

- `port`  
This is the port that the Web-API runs on. Please note that usually low port numbers (<1024) are
reserved for the operating system, so if possible try and use something higher.
