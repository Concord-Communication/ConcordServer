# WebServer
A Concord server for the web, exposing websocket and REST interfaces for communication.

## Setup
Follow these steps to set up your own Concord server.


Generate the necessary keys like so:
```
openssl genrsa -out private_key.pem 2048
openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt
openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der
```
