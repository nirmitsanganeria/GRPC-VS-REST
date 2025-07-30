## Prerequisites
Java 17 (or higher)

Apache Maven

## 1. Build the Project

Open a terminal in the root grpc-vs-rest folder and run this single command to build all the applications:

Bash


mvn clean install

## 2. Run the Services

You will need four separate terminal windows.


In Terminal 1 (Start the gRPC Server):

Bash


cd grpc-server

java -jar target/grpc-server-0.0.1-SNAPSHOT.jar

In Terminal 2 (Start the REST/JSON Server):

Bash


cd rest-server

java -jar target/rest-server-0.0.1-SNAPSHOT.jar


In Terminal 3 (Start the REST/Protobuf Server):

Bash


cd proto-rest-server

java -jar target/proto-rest-server-0.0.1-SNAPSHOT.jar

## 3. Run the Performance Test

After the three servers have started, open your fourth terminal.


Bash


cd test-client

java -jar target/test-client-0.0.1-SNAPSHOT.jar

This terminal will wait a few seconds, run the performance tests, print the final results table, and then exit.
