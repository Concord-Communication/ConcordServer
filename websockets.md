# Concord Websocket Protocol
The Concord web server relies on websockets to send real-time updates to clients regarding chat messages, user activity, and any other changes to the state of the server. This file defines the types of messages which can sent and received by a client that's connected to the server.

All messages' structure is defined by the `type` attribute of the message; this should be present in any message the server sends or receives.

## Server Messages
These are messages which the server sends to update clients about the state of the server.

**Typing** - Sent when a client indicates that they're typing a message in a channel.
```json
{
  "type": "chat_typing",
  "userId": 123,
  "channelId": 456,
  "threadId": null,
  "sentAt": 1234567
}
```

**Chat Sent** - Sent when a new chat message has been sent in a channel.
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

**Chat Deleted** - Sent when a chat message has been deleted from a channel.
```json
{
  "type": "chat_deleted",
  "chatId": 1234,
  "channelId": 123,
  "threadId": null
}
```

**Chat Edited** - Sent when a chat message is edited.
```json
{
  "type": "chat_edited",
  "chatId": 1234,
  "channelId": 123,
  "threadId": null,
  "content": "This is my edited chat message content!"
}
```

**Reactions Updated** - Sent when a chat's set of reactions are updated.
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

## Client Messages
These are messages which the client can send to the server to perform certain interactions quickly. Note that most of these types of messages are simply for convenience, and may be sent via the REST API.

**Typing** - Sent when client is typing a message to a channel. _Cannot be sent via the REST API._
```json
{
  "type": "chat_typing",
  "channelId": 123,
  "threadId": null,
  "sentAt": 123456789
}
```

**Chat Written** - Sent when a client wants to send a chat message to a channel.
```json
{
  "type": "chat_written",
  "channelId": 123,
  "threadId": null,
  "sentAt": 123456789,
  "content": "This is my message!"
}
```

**Chat Reaction** - Sent when a client adds or removes a reaction to a chat message. Set `adding` to false indicates that you want to remove the specified reaction.
```json
{
  "type": "chat_reaction",
  "chatId": 1234,
  "reaction": "😂",
  "adding": true
}
```
