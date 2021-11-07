# Concord Server

> For API documentation, please visit the respective [REST](../rest-api/) and [Websocket](../websocket-api/) pages.

The Concord Server is a high-performance asynchronous web server that supports both a rich HTTP REST API, and a real-time Websocket API for event propagation, for the single purpose of creating a real-time communication platform that does away with those pesky limitations imposed by commercial apps.

**Use Concord Server for whatever you want.**

> Legally speaking, Concord is not responsible for any of the user-generated content on your server.

To get up and running, check out the [quick-start guide](quick-start.md).

## System Overview

Concord is designed to be a completely open, transparent communication platform that's efficient and easy to use. To accomplish this goal, we provide a server that you can run out-of-the-box, as well as a number of official client applications for various platforms. The server is built using Java 17 and [Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) for powering its asynchronous REST and Websocket APIs, and uses MongoDB as its primary data source.
