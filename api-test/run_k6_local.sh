#!/bin/bash

# To run k6 load tests on your local pc

# Install postman-to-k6 https://github.com/grafana/postman-to-k6
# Install k6 https://k6.io/docs/getting-started/installation/
# https://k6.io/docs/using-k6/http-debugging/

if [[ "$(pwd)" =~ .*"api-test".* ]]; then
    cd ..
fi
postman-to-k6  api-test/ApiConfig.postman_collection.json -e api-test/local.postman_environment.json -o ./k6-script.js
k6 run --http-debug=full --vus 2 --duration 5s ./k6-script.js
