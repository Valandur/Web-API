# Web-API AdminPanel

The admin panel provides an access point for server admins and moderators to easily manage their 
server without having to be online/in minceraft or connected to the server through ssh.


## Table of Contents
1. [Creating users](#creating-users)
1. [Modifying users](#modifying-users)


<a name="creating-users"></a>
## Creating users

To create a new user for the admin panel enter `/webapi users add [name]` in your server console.
This will create a new user with the `[name]` specified and a random password. The password will 
be shown in the server console. If you want to specify the password for the user you can use the
command `/webapi users add [name] [password]`.

> The permission required to create new users in Web-API is `webapi.users.add`


<a name="modifying-users"></a>
## Modifying users (changing passwords, etc.)

To change the password use the `/webapi users pw [name] [newpassword]` command.  
To change the permissions for a user you need to edit the `/config/webapi/user.conf` config file.
You can also delete a user from this file to remove them.
