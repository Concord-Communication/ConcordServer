# Images

## Get Image

`GET /images/:imageId`

Fetches an image with the given id.

This endpoint responds with a `Content-Type` corresponding to the image's file type, such `image/png` or `image/jpeg`. **The response will not be JSON**, but rather the raw image data.