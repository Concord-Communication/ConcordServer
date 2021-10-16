# WebServer
A Concord server for the web, exposing websocket and REST interfaces for communication.

## Setup
Follow these steps to set up your own Concord server.

First, Concord makes use of [MongoDB](https://www.mongodb.com/) as its primary database, so you will need to have an instance of this running. By default, the server is configured to connect to MongoDB at `localhost:27017` with the username `root` and password `example` (because these credentials are used for the development database, see `docker-compose.yaml`). It is highly encouraged to use more secure credentials. You can do this by defining an external `application.properties` file in the server's working directory, or in a `config/` directory within the working directory. Define your database URI like so:

```properties
spring.data.mongodb.uri=mongodb://root:example@localhost:27017/concord
```

The URI is of the form `mongodb://<user>:<password>@<host>:<port>/concord`; fill in your own database's values here.

Next, in order to issue JSON Web Tokens as authentication credentials to users that connect to your server, you'll need to generate a private key that the application uses to sign all issued tokens.

Generate the private key like so:
```
openssl genrsa -out private_key.pem 2048
openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt
```

If you want to have the ability for other services to accept tokens issued by Concord, you'll also need to give those services the public key to Concord's private key:

```
openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der
```

Finally, when starting up, you'll want to provide the `--reset` command line argument on the first run, as this will prepare a default admin user for you to use.

## Protocol and Documentation
The Concord web server offers both a REST API and a websocket API. All actions can be performed by the client via the REST API, with the websocket API intended mainly as a way for the client to receive real-time updates from the server, with some limited capabilities for client interaction.

### REST API
The REST API is documented as a [Postman](https://www.postman.com/) collection which you can download via the `ConcordWeb.postman_collection`; just import this into Postman, and explore the endpoints.

All endpoints are located under `https://<server_base_url>/api`.

#### Authentication
Most API endpoints require the user to be authenticated. To access these, please obtain an access token by sending the following request:
```
POST /api/tokens
{
    "username": "example_guy",
    "password": "hunter2"
}
```
All subsequent requests should contain this token in the `Authorization` header in the form `Bearer <token>`.

### Websocket API
The websocket API is accessible via `wss://<server_base_url>/client`. More information on the exact protocol which the server uses to communicate via the websocket is available in `websockets.md`.

#### Authentication
All clients that connect to the websocket **must** be authenticated; that is, they must provide a valid access token when initiating the connection. Because the native Javascript websocket implementation does not yet allow for this to be passed via a header, the token must be sent via a `token` query parameter. For example, `wss://localhost:8080/client?token=abc`.
