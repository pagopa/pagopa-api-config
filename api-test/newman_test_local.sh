#!/bin/bash

# Run only in local environment
if [[ "$(pwd)" =~ .*"api-test".* ]]; then
    cd ..
fi
newman run api-test/ApiConfig.postman_collection.json --environment=api-test/local.postman_environment.json --reporters cli,junit --reporter-junit-export Results/api-config-TEST.xml
