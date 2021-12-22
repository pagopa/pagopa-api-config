#!/bin/bash

STAGE=$1
EXEC=$2
case $STAGE in

  d) # develop
    HOSTPORT="https://api.dev.platform.pagopa.it"
    BASEPATH="apiconfig/api/v1"
    CLIENT_ID=$DEV_APICONFIG_CLIENT_FE_ID
    CLIENT_SECRET=$DEV_APICONFIG_CLIENT_FE_SECRET
    RESOURCE=$DEV_APICONFIG_CLIENT_BE_RESOURCE
    ;;

  t) # test with H2
    HOSTPORT="https://api.dev.platform.pagopa.it"
    BASEPATH="apiconfig/api/v1"
    CLIENT_ID=$DEV_APICONFIG_CLIENT_FE_ID
    CLIENT_SECRET=$DEV_APICONFIG_CLIENT_FE_SECRET
    RESOURCE=$DEV_APICONFIG_CLIENT_BE_RESOURCE
    ;;

  u) # uat
    HOSTPORT="https://api.uat.platform.pagopa.it"
    BASEPATH="apiconfig/api/v1"
    CLIENT_ID=$UAT_APICONFIG_CLIENT_FE_ID
    CLIENT_SECRET=$UAT_APICONFIG_CLIENT_FE_SECRET
    RESOURCE=$UAT_APICONFIG_CLIENT_BE_RESOURCE
    ;;

  p) # production
    HOSTPORT="https://api.platform.pagopa.it"
    BASEPATH="apiconfig/api/v1"
    CLIENT_ID=$PROD_APICONFIG_CLIENT_FE_ID
    CLIENT_SECRET=$PROD_APICONFIG_CLIENT_FE_SECRET
    RESOURCE=$PROD_APICONFIG_CLIENT_BE_RESOURCE
    ;;

  *) # local
    HOSTPORT="http://localhost:8080"
    BASEPATH="apiconfig/api/v1"
    ;;
esac

# change env json
jq ".values[0].value = \"${HOSTPORT}\" | .values[1].value = \"${BASEPATH}\" | .values[2].value = \"${TENANT_ID}\" | .values[3].value = \"${CLIENT_ID}\" | .values[4].value = \"${CLIENT_SECRET}\" | .values[5].value = \"${RESOURCE}\"" api-test/Azure.postman_environment.json > api-test/tmp.json && mv api-test/tmp.json api-test/Azure.postman_environment.json


if [ -z "$EXEC" ]
then
  echo "No test run !"
else
  if [[ "$EXEC" =~ ^(int|load)$ ]]; then
      if [ "$EXEC" = "load" ]
      then
          postman-to-k6 api-test/ApiConfig.postman_collection.json --environment api-test/Azure.postman_environment.json -o ./k6-script.js
          k6 run --vus 2 --duration 30s ./k6-script.js
      else
          newman run api-test/ApiConfig.postman_collection.json --environment=api-test/Azure.postman_environment.json --reporters cli,junit --reporter-junit-export Results/api-config-TEST.xml
      fi
  else
      echo "Warning: No test run!"
      exit 0
  fi
fi


