# Gateway Tracing Example

This project is for the demonstration of a bug where Logbook logs don't contain traceIds and spanIds for "Incoming Request" and "Incoming Response" logs unless deprecated classes are used.

You can either start the Application, call http://localhost:9999/get in a browser and check the logs or run the Unit Tests in LoggingTest.java.

Only when the Spring Profile "fixTracing" is active, all Logbook logs will contain the correct tracing information in the MDC map.