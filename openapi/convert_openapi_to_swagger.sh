# This script converts the autogenerated OpenApi 3 specifications into Swagger 2.
# This is useful to create the client with gen-api-models https://github.com/pagopa/openapi-codegen-ts

# how install api-spec-converter https://www.npmjs.com/package/api-spec-converter
api-spec-converter  --from=openapi_3 --to=swagger_2 ./openapi.json > swagger.json

# BugFix for api-spec-converter: swagger 2 does not support http as type
sed -i '' 's/\"type\": \"http\"/\"type\": \"apiKey\"/g' swagger.json

