postman-to-k6  ./ApiConfig.postman_collection.json -e ./Azure.postman_environment.json -o k6-script.js
k6 run --vus 10 --duration 5s ./k6-script.js
