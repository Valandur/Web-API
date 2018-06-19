# Web-API Messages

Message are pieces of text that can be sent to players, if required with clickable options for
the player to interact with.


## Table of Contents
1. [Format](#format)
1. [Responses](#responses)


<a name="format"></a>
## Format

You must send a `POST` request to the `/message` endpoint.
The body of the request must have the following format:

```json
{
  "id": "unique-id",
  "target": "player-uuid",
  "targets": [ "player-1-uuid", "player-2-uuid"],
  "message": "Hello world! Testing the Web-API messages :D",
  "once": true,
  "options": [{
    "key": "option1",
    "value": "Option 1"
  }, {
    "key": "other_opt",
    "value": "[Other Option]"
  }]
}
```

The `id` is a unique identifier that the Web-API will send back to your server. You can use
this to identify different messages. You can put anything you like here, or leave it blank
in case you don't need it.

The `target` is the UUID of the player you wish to send the message to. Sending messages
to offline players is not possible. If you want to send a message to multiple players use 
the `targets` array, which is an array of UUIDs. You must specify either `target` or `targets`.

`message` is the content of your message - the actual text sent to the player in chat.
You can use the [Ampersand formatting](https://docs.spongepowered.org/stable/en-GB/plugin/text/representations/formatting-code-legacy.html#ampersand-formatting)
to apply text formatting to the message. Your message must have a message...

If the `once` option is set to true then the player will only be able to reply to the message
once (assuming it has clickable options). Otherwise, if missing or set to false, the player will
be able to click the provided options multiple times.

The `options` array contains all the clickable options the player can choose from.
Each option has a `key` and a `value`. The `key` is used internally, and sent back to your
server if the player clicks that option. The `value` is what is displayed to the player
in the text.


<a name="responses"></a>
## Responses

To receive the responses (when players click one of the provided options) you must add a
[WebHook](WEBHOOKS.md) for **INTERACTIVE_MESSAGE**. (previously CUSTOM_MESSAGE)

The response will look similar to the following:
```json
{
  "id": "your_id",
  "source": "The UUID of the player that selected this response",
  "choice": "The key of the option that the player selected"
}
```

The `id` is the id that you specified when sending the message as described above.

The `source` is the UUID of the player that clicked on the option.

The `choice` is the **key** of the option that the player clicked on.
