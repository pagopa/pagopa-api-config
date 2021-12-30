#!/usr/bin/env bash

# To run k6 load tests on your local pc

# Install postman-to-k6 https://github.com/apideck-libraries/postman-to-k6
# Install k6 https://k6.io/docs/getting-started/installation/
# https://k6.io/docs/using-k6/http-debugging/

if [[ "$(pwd)" =~ .*"api-test".* ]]; then
    cd ..
fi

bash api-test/postman-to-k6-converter.sh api-test/local.postman_environment.json

## execute script
k6 run --vus 2 --duration 5s ./k6-script.js
