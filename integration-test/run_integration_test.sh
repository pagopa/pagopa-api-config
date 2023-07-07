#!/bin/bash

# example: sh ./run_integration_test.sh <local|dev|uat|prod> <sub-key>

set -e

# run integration tests
cd ./src || exit
yarn install
yarn test:"$1"
