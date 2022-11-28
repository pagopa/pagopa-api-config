#!/bin/bash

# This script converts the autogenerated OpenApi 3 specifications into Swagger 2.
# This is useful to create the client with gen-api-models https://github.com/pagopa/openapi-codegen-ts

# how install api-spec-converter https://www.npmjs.com/package/api-spec-converter

if [[ "$(pwd)" =~ .*"openapi".* ]]; then
    cd ..
fi

if [ $(npm list -g | grep -c api-spec-converter) -eq 0 ]; then
  echo "YEEE"
  npm install -g api-spec-converter
fi

if ! $(curl --output /dev/null --silent --head --fail http://localhost:8080/apiconfig/api/v1/info); then
  mvn spring-boot:run -Dmaven.test.skip=true -Dspring-boot.run.profiles=h2 &
fi

# waiting the service
printf 'Waiting for the service'
attempt_counter=0
max_attempts=50
until $(curl --output /dev/null --silent --head --fail http://localhost:8080/apiconfig/api/v1/info); do
    if [ ${attempt_counter} -eq ${max_attempts} ];then
      echo "Max attempts reached"
      exit 1
    fi

    printf '.'
    attempt_counter=$((attempt_counter+1))
    sleep 5
done
echo 'Service Started'


curl http://127.0.0.1:8080/apiconfig/api/v1/v3/api-docs > ./openapi/openapi.json
api-spec-converter  --from=openapi_3 --to=swagger_2 ./openapi/openapi.json > ./openapi/swagger.json

# BugFix for api-spec-converter: swagger 2 does not support http as type
sed -i '' 's/\"type\": \"http\"/\"type\": \"apiKey\",\n      \"in\": \"header\",\n      \"name\": \"Authorization\" /g' ./openapi/swagger.json

# BugFix for multipart/form-data
jq  '."paths"."/icas/xsd".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/icas".post.parameters[1].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/icas/check".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/counterparttables".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/cdis".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/cdis/check".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/batchoperation/creditorinstitution-station/loading".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/batchoperation/creditorinstitution-station/migration".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/icas/check/massive".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/icas/massive".post.parameters[1].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json

kill %% || true
