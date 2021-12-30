#!/usr/bin/env bash

postman_environment=$1
if [ -z "$1" ]
  then
    postman_environment="api-test/local.postman_environment.json"
fi
echo "postman_environment=$postman_environment"

## convert postman collection into k6 script
postman-to-k6  api-test/ApiConfig.postman_collection.json -e ${postman_environment} -o ./k6-script.js

## modify generated script to solve file upload problem
# add import 'import { FormData } from "./api-test/formData-k6-dep.js"'
awk '{gsub("import { group } from \"k6\";", "import { group } from \"k6\";\nimport { FormData } from \"./api-test/formData-k6-dep.js\"", $0); print}' ./k6-script.js > ./k6-script.js.tmp
# retrieve files definition generated by postman (i.e. files["src/test/resources/file/ica_valid.xml"])
files=$(awk '/files\["[\w\W]*/ {print $1;exit;}' ./k6-script.js.tmp)
# define code about formData and append the previous file
# the output should be something like that
#    const f = new FormData()
#    f.append("file", files["src/test/resources/file/ica_valid.xml"]);
formdata="\n\tconst f = new FormData()\n\tf.append(\"file\", ${files});"
# retrieve the row where checkXSD postman request is defined
row=$(grep -n "name: \"checkXSD\"" ./k6-script.js.tmp | cut -f1 -d:)
# add before the previous request the formData code
awk -v n=$(($row-2)) -v s="${formdata}" 'NR==n {print s} {print}' ./k6-script.js.tmp > ./k6-script.js.tmp.2
mv ./k6-script.js.tmp.2 ./k6-script.js.tmp
# remove the data attribute generated by postman conversion
row=$(fgrep -n "file: ${files}" ./k6-script.js.tmp | cut -f1 -d:)
awk -v b=$(($row-1)) -v c=$row -v f=$(($row+1)) '{if(NR!=b && NR!=c && NR!=f){print $0}}' ./k6-script.js.tmp > ./k6-script.js.tmp.2
mv ./k6-script.js.tmp.2 ./k6-script.js.tmp
# define code about data and headers
# the output should be something like that
#    data: f.body(),
#    headers: {
#      "Content-Type": "multipart/form-data; boundary="+f.boundary
#    },
data="\t  data: f.body(),\n\t  headers: {\n\t\t\"Content-Type\": \"multipart/form-data; boundary=\"+f.boundary\n\t  },"
# add the previous code in the k6 postman request
awk -v r=$(($row-1)) -v d="${data}" 'NR==r{print d}1' ./k6-script.js.tmp > ./k6-script.js.tmp.2
mv ./k6-script.js.tmp.2 ./k6-script.js
rm ./k6-script.js.tmp
