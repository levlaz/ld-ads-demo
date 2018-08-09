# ld-ads-demo

Reference Implementation for LaunchDarkly Analytics Data Stream Consumer

## Usage 

You must have the Analytics Data Stream enabled in your account in order to
test this software. 

1. A valid `LD_SDK_KEY` exported as an environment variable. This key should 
map back to the specific environment of a specific project that you want to 
get events for. 

We use a Makefile to make it easier to build and run this project. 

### Writing Events to PostgreSQL 

If you want to write events to PostgreSQL, set the `FLYWAY_URL` environment variable to point to your PostgreSQL database and use the `postgres` connector option when you run the application. Once the app starts up it will apply any pending migrations and begin to write events to the database. 

### Building 

You can run `mvn compile assembly:single` to build this project for the first time. 

### Running 

You can run `java -jar target/ld-ads-demo-1.0-SNAPSHOT-jar-with-dependencies.jar` to run the project with the default connector. 

### Connectors
If you want to use a different connector you can pass the name of the connector
as a string argument. 

`java -jar target/ld-ads-demo-1.0-SNAPSHOT-jar-with-dependencies.jar -c $CONNECTOR`

The supported connectors right now are: 

* `console` - pretty print to console
* `postgres` - write summary and index events to postgresql 

You will now see events coming through the system in your console. 