# Chat Messages

These are messages which are sent regarding chat events that have occurred.

Many of these messages contain some of the same types of data:

- `channelId` - The id of the channel that the event occurred in. This should **never be null**.
- `threadId` - The id of the thread that the event occurred in. This **can be null**, for events that pertain to the channel itself.

## `chat_typing`

Sent by the server when a client indicates that they're typing a message in a channel.

```json
{
  "type": "chat_typing",
  "userId": 123,
  "channelId": 456,
  "threadId": null,
  "sentAt": 1234567
}
```

Additionally, clients can send this message to the server without the `userId` attribute, to indicate that their user is currently typing in a channel. Here's an example of a client's `chat_typing` message:

```json
{
  "type": "chat_typing",
  "channelId": 123,
  "threadId": null,
  "sentAt": 123456789
}
```

> If a client sends a `userId` attribute, it will be ignored. If the client doesn't send a `sentAt` attribute, the server uses the timestamp when the message was received.

## `chat_sent`

Sent by the server when a new chat message has been sent in a channel.

```json
{
  "type": "chat_sent",
  "chat": {
    "id": 123,
    "createdAt": 123456789,
    "authorId": 1234,
    "channelId": 123,
    "threadId": null,
    "content": "Hello, this is my message!",
    "edited": false,
    "reactions": {}
  }
}
```

## `chat_deleted`

Sent when a chat message has been deleted from a channel.

```json
{
  "type": "chat_deleted",
  "chatId": 1234,
  "channelId": 123,
  "threadId": null
}
```

## `chat_edited`

Sent when a chat message is edited.

```json
{
  "type": "chat_edited",
  "chatId": 1234,
  "channelId": 123,
  "threadId": null,
  "content": "This is my edited chat message content!"
}
```

## `chat_reactions_updated`

Sent when a chat's set of reactions are updated.

```json
{
  "type": "chat_reactions_updated",
  "chatId": 1234,
  "channelId": 123,
  "threadId": null,
  "reactions": {
    "😂": 5,
    "✔️": 2
  }
}
```

## `chat_written`

Sent when a client wants to send a chat message to a channel.

```json
{
  "type": "chat_written",
  "channelId": 123,
  "threadId": null,
  "sentAt": 123456789,
  "content": "This is my message!"
}
```

## `chat_reaction`

Sent when a client adds or removes a reaction to a chat message. Set `adding` to false indicates that you want to remove the specified reaction.

```json
{
  "type": "chat_reaction",
  "chatId": 1234,
  "reaction": "😂",
  "adding": true
}
```

