# Web-API Commands

All commands begin with `/webapi`  
<> denotes **required** parameters  
[] denotes *optional* parameters  
[...] denotes *multiple optional* parameters  


## AdminPanel Users

| Command | Permission | Description |
|---------|------------|-------------|
| users list | webapi.user.list | Lists all the users of the [AdminPanel](ADMIN_PANEL.md) |
| users add \<user> [password] | webapi.user.add | Adds a new [AdminPanel](ADMIN_PANEL.md) user |
| users pw \<user> <password> | webapi.user.changepw | Changes the password of an [AdminPanel](ADMIN_PANEL.md) user |


## Whitelist

| Command | Permission | Description |
|---------|------------|-------------|
| whitelist add \<ip> | webapi.whitelist.add | Adds the specified IP to the whitelist |
| whitelist remove \<ip> | webapi.whitelist.remove | Removes the specified IP from the whitelist |
| whitelist enable | webapi.whitelist.enable | Enables the whitelist for Web-API |
| whitelist disable | webapi.whitelist.disable | Disables the whitelist for Web-API |


## Blacklist

| Command | Permission | Description |
|---------|------------|-------------|
| blacklist add \<ip> | webapi.blacklist.add | Adds the specified IP to the blacklist |
| blacklist remove \<ip> | webapi.blacklist.remove | Removes the specified IP from the blacklist |
| blacklist enable | webapi.blacklist.enable | Enables the blacklist for Web-API |
| blacklist disable | webapi.blacklist.disable | Disables the blacklist for Web-API |


## Block Operations

| Command | Permission | Description |
|---------|------------|-------------|
| ops list | webapi.op.list | List all running block operations |
| ops pause \<uuid> | webapi.op.pause | Pause/Resume the specified block operation |
| ops stop \<uuid> | webapi.op.stop | Stops the specified block operation |


## Notify commands / WebHooks

| Command | Permission | Description |
|---------|------------|-------------|
| notify \<name> [params...] | webapi.notify.\<name> | Triggers the \<name> [WebHook](WEBHOOKS.md) |