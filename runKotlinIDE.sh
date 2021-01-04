#!/bin/bash

cd kotlinIDE
mvn install
java -jar target/kotlinIDE-0.0.1-SNAPSHOT.jar &
cd ..
cd client
npm install
npm start &
