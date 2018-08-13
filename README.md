# ld-ads-demo

Reference Implementation for LaunchDarkly Analytics Data Stream Consumer

## Quickstart 

You must have the Analytics Data Stream enabled in your account in order to
test this software. 

1. Clone this repo 
2. `cd` into the cloned repo 
3. Copy `.env.example` to `.env`
4. Enter the SDK key for the environment that you want to consume events for. 
5. Run `mvn compile assembly:single`
6. Run `java -jar target/ld-ads-demo-1.0-SNAPSHOT-jar-with-dependencies.jar`

You will now see events in your console as they occur. 

## Usage 

### Change Logger Level 

Add an argument after `java` with the desired log level. 

```
-Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

### Writing Events to PostgreSQL 

If you want to write events to PostgreSQL, set the `FLYWAY_URL` environment variable to point to your PostgreSQL database and use the `postgres` connector option when you run the application. Once the app starts up it will apply any pending migrations and begin to write events to the database. 

### Building 

You can run `mvn compile assembly:single` to build this project. Do this initially and if you make changs to the code.

### Running 

You can run `java -jar target/ld-ads-demo-1.0-SNAPSHOT-jar-with-dependencies.jar` to run the project with the default connector. 

### Connectors
If you want to use a different connector you can pass the name of the connector
as a string argument. 

`java -jar target/ld-ads-demo-1.0-SNAPSHOT-jar-with-dependencies.jar -c $CONNECTOR`

The supported connectors right now are: 

* `console` - pretty print to console
* `postgres` - write summary and index events to postgresql 

