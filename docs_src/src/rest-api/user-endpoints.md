# User Endpoints

## Register New User

`POST /users/register`

Register a new user with the given credentials. Will respond with a **400 Bad Request** if the username is already taken by another user.

**This is endpoint is publicly accessible. Do not provide an Authorization header.**

Sample payload:

```json
{
    "username": "johnny",
    "password": "testpass"
}
```

Sample success response:

```json
{
    "id": 8501928463962112,
    "username": "johnny"
}
```

## Get Token

`POST /tokens`

Obtains a new access token using a user's credentials. Will return a **400 Bad Request** if invalid credentials were given.

**This is endpoint is publicly accessible. Do not provide an Authorization header.**

Sample payload:

```json
{
    "username": "admin",
    "password": "kiSnfO4U5GUvdxDYWT7omb7eA2zHyuja7kS7BJgX"
}
```

Sample success response:

```json
{
    "token": "abcdefg"
}
```

## Recycle Token

`GET /tokens/recycle`

Obtains a new access token using a user's current access token as credentials.

Sample success response:

```json
{
    "token": "abcdefg"
}
```

## Get Users

`GET /users`

Gets a list of all users registered with this Concord server.

Sample success response:

```json
[
    {
        "id": 7263488141430784,
        "username": "admin",
        "profile": {
            "createdAt": 1635598950622,
            "nickname": "admin",
            "bio": null,
            "avatarId": 7645045389791232
        },
        "status": {
            "onlineStatus": "OFFLINE"
        }
    },
    {
        "id": 8501928463962112,
        "username": "johnny",
        "profile": {
            "createdAt": 1635894217786,
            "nickname": "johnny",
            "bio": null,
            "avatarId": null
        },
        "status": {
            "onlineStatus": "OFFLINE"
        }
    }
]
```

## Get User

`GET /users/:userId`

Gets information about a single user.

Sample success response:

```json
{
    "id": 7263488141430784,
    "username": "admin",
    "profile": {
        "createdAt": 1635598950622,
        "nickname": "admin",
        "bio": null,
        "avatarId": 7645045389791232
    },
    "status": {
        "onlineStatus": "OFFLINE"
    }
}
```

> Note: The user's avatar image can be fetched via [`/images/<avatarId>`](images.md#get-image).