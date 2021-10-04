#!/bin/bash

STAGE=$1
EXEC=$2
case $STAGE in

  d) # develop
    HOSTPORT="https://api.dev.platform.pagopa.it"
    BASEPATH="apiconfig/api"
    ;;

  u) # uat
    HOSTPORT="https://api.uat.platform.pagopa.it"
    BASEPATH="apiconfig/api"
    ;;

  p) # production
    HOSTPORT="https://api.platform.pagopa.it"
    BASEPATH="apiconfig/api"
    ;;

  *) # local
    HOSTPORT="http://localhost:8080"
    BASEPATH="apiconfig/api/v1"
    ;;
esac

# change env json
jq ".values[0].value = \"${HOSTPORT}\" | .values[1].value = \"${BASEPATH}\"" api-test/Azure.postman_environment.json > api-test/tmp.json && mv api-test/tmp.json api-test/Azure.postman_environment.json


if [ -z "$EXEC" ]
then
  echo "No test run !"
else
  if [[ "$EXEC" =~ ^(int|load)$ ]]; then
      if [ "$EXEC" = "load" ]
      then
          postman-to-k6 api-test/ApiConfig.postman_collection.json --environment api-test/Azure.postman_environment.json -o generated/script.js
          k6 run --vus 2 --duration 30s generated/script.js
      else
          newman run api-test/ApiConfig.postman_collection.json --environment=api-test/Azure.postman_environment.json --reporters cli,junit --reporter-junit-export Results/api-config-TEST.xml
      fi
  else
      echo "Warning: No test run!"
      exit 0
  fi
fi


