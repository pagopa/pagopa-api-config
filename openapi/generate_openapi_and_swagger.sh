#!/bin/bash

# This script converts the autogenerated OpenApi 3 specifications into Swagger 2.
# This is useful to create the client with gen-api-models https://github.com/pagopa/openapi-codegen-ts

# how install api-spec-converter https://www.npmjs.com/package/api-spec-converter

if [[ "$(pwd)" =~ .*"openapi".* ]]; then
    cd ..
fi


curl http://127.0.0.1:8080/apiconfig/api/v1/v3/api-docs | python3 -m json.tool > ./openapi/openapi.json
api-spec-converter  --from=openapi_3 --to=swagger_2 ./openapi/openapi.json > ./openapi/swagger.json

# BugFix for api-spec-converter: swagger 2 does not support http as type
sed -i '' 's/\"type\": \"http\"/\"type\": \"apiKey\",\n      \"in\": \"header\",\n      \"name\": \"Authorization\" /g' ./openapi/swagger.json

# BugFix for multipart/form-data
jq  '."paths"."/icas/xsd".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/icas".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/counterparttables".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/cdis".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/batchoperation/creditorinstitution-station/loading".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
jq  '."paths"."/batchoperation/creditorinstitution-station/migration".post.parameters[0].type |= "file"' ./openapi/swagger.json > ./openapi/swagger.json.temp && mv ./openapi/swagger.json.temp ./openapi/swagger.json
