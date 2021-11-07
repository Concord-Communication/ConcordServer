# Concord Websocket API

The Concord web server relies on websockets to send real-time updates to clients regarding chat messages, user activity, and any other changes to the state of the server. This documentation defines the types of messages which can sent and received by a client that's connected to the server.

All messages follow a simple structure that's defined by the `type` attribute of the message; this should be present in any message the server sends or receives. The value of `type` indicates what message has been sent, and consequently, what structure it will have.

## Connecting

To connect to a Concord server's websocket endpoint, follow these steps:

1. Obtain a valid access token from the server's [`/api/tokens`](/rest-api/) endpoint.
2. Open a new websocket connection to a URL formatted like `wss://<server-url>/client?token=<access-token>`, where `<server-url>` is replaced with your desired Concord server's URL, and `<access-token>` is replaced with your access token.

> Note that while most Concord servers should be accessible via HTTPS/WSS, it is not required. If the Concord server you're trying to connect to does not use a secure connection, you must start your websocket URL with `ws://` instead of `wss://`.

Here's a barebones Javascript example that connects to a Concord server running on `localhost:8080`, and simply logs all received messages to the console:

```javascript
const token = "YOUR-TOKEN-HERE";
let ws = new WebSocket("ws://localhost:8080/client?token=" + token);
ws.onopen = () => console.log("Opened websocket connection.");
ws.onmessage = (msg) => console.log(msg.data);
ws.onclose = () => console.log("Closed.");
```

## Standard Practices

When sending and receiving messages, the Concord server will make some common assumptions about the data that's being transferred.

### Format

All messages are sent by the server in JSON format, using UTF-8 character encoding. It is assumed that all messages sent by clients will also be in JSON format, using the UTF-8 encoding. Messages sent by clients which do not follow this format may receive an error response, or they may be ignored by the server.

### Timestamps

Unless otherwise denoted for a particular message type, **all timestamps are in milliseconds since the Unix epoch**. You can usually find timestamps by their `...At` suffix, like with `sentAt`, `createdAt`, `updatedAt`, etc. No time zone information is provided.

### Nullable Attributes

Unless otherwise denoted, **all attributes are assumed to be non-null**. This applies to both messages sent by the server, and those sent by clients. You may assume that providing a null value where it is not explicitly denoted as being allowed may result in an error response, or the server may choose to ignore the message.