# Server Quick Start Guide

Read this quick (~5 minute) guide to get your Concord server up and running.

## Prerequisites

There are a few things that you Concord server will need to function properly, so set these up before you start up your server.

### MongoDB

Concord makes use of [MongoDB](https://www.mongodb.com/) as its primary database, so you will need to have access to an instance of this for it to connect to. By default, the server is configured to connect to MongoDB at `localhost:27017` using `root` and the password `example` as its credentials.

Any MongoDB database will work, as long as your machine can reach it. Generally though, the two most popular options are to use a self-hosted instance of MongoDB using their [Community Server](https://www.mongodb.com/try/download/community), or MongoDB's own [Atlas](https://www.mongodb.com/atlas) online database.

All you need to do is specify the URL that Concord can use to connect to the database in your server's config file, like so:

```properties
spring.data.mongodb.uri=mongodb://root:example@localhost:27017/concord
```

::: tip

You can also run MongoDB in a docker container! [Check here for an example docker-compose file.](https://github.com/Concord-Communication/ConcordServer/blob/main/docker-compose.yaml)

:::

### Authentication

In order for your Concord server to be able to issue valid JWT access tokens to users, it needs to sign these with a private key. Use the following bash script to generate a private key file in `DER` format.

```bash
openssl genrsa -out private_key.pem 2048
openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt
```

::: tip

If you want other services to be able to accept tokens issued by Concord, you'll also need to give those services the public key to Concord's private key. You can generate one like so:

```bash
openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der
```

:::

## Startup & Configuration

When starting the server for the first time, provide the `--reset` flag, which will tell the server to generate all of the basic server information, and a new admin user with a randomly-generated password. You can use this user's credentials to log in. It's highly advised that you change the password to this account as soon as possible.