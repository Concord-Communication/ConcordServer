# Concord REST API

The Concord server provides users with access to a REST API that allows for fetching and updating of server data, beyond what is capable with the [websocket API](/websocket-api/).

## Standards

It is assumed that all requests and responses will be formatted in JSON, unless otherwise specified.

It is assumed that all path variables are required.

It is assumed that all request parameters are optional, unless otherwise specified.

All requests to the REST API are made under the `<base-url>/api` path, thus this prefix is omitted from all requests' documentation. For example, documentation for a `/users` endpoint would translate to your application sending a request to `http://localhost:8080/api/users`.

## Authentication

With the exception of just a few endpoints, **all endpoints in the Concord REST API require authentication**. This API uses JWT authentication, where an access token must be provide via HTTP headers with each request.

You may obtain an access token via the [Get Token endpoint](/rest-api/user-endpoints#get-token) by supplying your user's username and password. This access token is valid for a limited time, after which you'll need to obtain a new token using your user's credentials again. Additionally, you may also request a new token using your existing token via the [Recycle Token endpoint](/rest-api/user-endpoints#recycle-token).

Your access token should be supplied as a *bearer token* in the `Authorization` HTTP header, like so:

```
Authorization: Bearer <token>
```

where `<token>` is replaced by your access token. Here's another example of building a valid HTTP request in Java:

```java
var url = "http://localhost:8080/api/users";
var request = HttpRequest.newBuilder(URI.create(url)).GET()
	.header("Authorization", "Bearer " + this.token)
	.header("Accept", "application/json")
	.build();
```

Concord server administrators may configure their own preferred expiration dates for issued tokens, and administrators also reserve the right to revoke access to any particular user's token at any time.

If you don't yet have the credentials needed to obtain an access token, you will need to use the [Register New User endpoint](/rest-api/user-endpoints#register-new-user) to register your user with a Concord server. The server may accept or reject this request. If it accepts, you will receive your user's id and username as response, which you can then use to obtain a token.

