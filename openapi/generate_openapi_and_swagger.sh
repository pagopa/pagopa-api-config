#!/bin/bash

# This script converts the autogenerated OpenApi 3 specifications into Swagger 2.
# This is useful to create the client with gen-api-models https://github.com/pagopa/openapi-codegen-ts

# how install api-spec-converter https://www.npmjs.com/package/api-spec-converter

if [[ "$(pwd)" =~ .*"openapi".* ]]; then
    cd ..
fi

mvn test -Dtest=OpenApiGenerationTest

if [ $(npm list -g | grep -c api-spec-converter) -eq 0 ]; then
  npm install -g api-spec-converter
fi

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
jq  '."paths"."/icas/massive".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/creditorinstitutions/ibans/csv".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/creditorinstitutions/ibans".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/creditorinstitutions/cbill".post.parameters[1].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json

kill %% || true
