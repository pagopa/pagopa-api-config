# To run k6 load tests on your local pc

# Install postman-to-k6 https://github.com/grafana/postman-to-k6
# Install k6 https://k6.io/docs/getting-started/installation/
postman-to-k6  ./ApiConfig.postman_collection.json -e ./local.postman_environment.json -o ./k6-script.js
k6 run --vus 2 --duration 5s ./k6-script.js
