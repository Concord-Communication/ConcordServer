# Server Configuration

This page provides a detailed description of all configuration settings that Concord Server accepts, including both the Concord-specific options and those that ship with Spring framework which might be useful for you as a Concord server admin.

Note that configuration of your server through configuration files is limited to low-level settings for how the application itself runs and behaves. For fine-grained control over the content and layout of your server's channels, roles, and more, please do so via the admin tools in your preferred Concord client application, or you can always manually play around with the server's [REST API](../rest-api/).

While it's acceptable to place configuration files anywhere the Spring framework supports, we recommend creating a `config` directory in your server's working directory, and placing an `application.properties` file inside it.

## Concord Settings

This section covers all configuration settings directly used for Concord, and does _not_ include those settings provided by the Spring framework.

- `concord.auth.token-validity-days` specifies the number of days for which an access token issued by your server will be valid. After this time has elapsed, clients will need to obtain a new access token using their username and password again.

## Spring Settings

This section covers some important configuration settings provided by the Spring framework.

- `spring.data.mongodb.uri` specifies the URI that Concord will use to connect to your MongoDB database. For more information about this, please see the [MongoDB](quick-start.md#mongodb) section of the quick-start guide.