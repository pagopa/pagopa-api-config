#!/bin/bash

# To run k6 load tests on your local pc

# Install postman-to-k6 https://github.com/apideck-libraries/postman-to-k6
# Install k6 https://k6.io/docs/getting-started/installation/
# https://k6.io/docs/using-k6/http-debugging/

if [[ "$(pwd)" =~ .*"api-test".* ]]; then
    cd ..
fi

# convert postman collection into k6 script
postman-to-k6  api-test/ApiConfig.postman_collection.json -e api-test/local.postman_environment.json -o ./k6-script.js

# modify generated script to solve file upload problem
awk '{gsub("import { group } from \"k6\";", "import { group } from \"k6\";\nimport { FormData } from \"./api-test/formData-k6-dep.js\"", $0); print}' ./k6-script.js > ./k6-script.js.tmp
files=$(awk '/files\["[\w\W]*/ {print $1;exit;}' ./k6-script.js.tmp)
formdata="\n\tconst f = new FormData()\n\tf.append(\"file\", ${files});"
row=$(grep -n "name: \"checkXSD\"" ./k6-script.js.tmp | cut -f1 -d:)
awk -v n=$(($row-2)) -v s="${formdata}" 'NR==n {print s} {print}' ./k6-script.js.tmp > ./k6-script.js.tmp.2
mv ./k6-script.js.tmp.2 ./k6-script.js.tmp
row=$(fgrep -n "file: ${files}" ./k6-script.js.tmp | cut -f1 -d:)
awk -v b=$(($row-1)) -v c=$row -v f=$(($row+1)) '{if(NR!=b && NR!=c && NR!=f){print $0}}' ./k6-script.js.tmp > ./k6-script.js.tmp.2
mv ./k6-script.js.tmp.2 ./k6-script.js.tmp
data="\t  data: f.body(),\n\t  headers: {\n\t\t\"Content-Type\": \"multipart/form-data; boundary=\"+f.boundary\n\t  },"
awk -v r=$(($row-1)) -v d="${data}" 'NR==r{print d}1' ./k6-script.js.tmp > ./k6-script.js.tmp.2
mv ./k6-script.js.tmp.2 ./k6-script.js
rm ./k6-script.js.tmp

# execute script
k6 run --vus 2 --duration 5s ./k6-script.js
