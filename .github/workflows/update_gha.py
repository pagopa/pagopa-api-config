import requests
import json


url = 'https://github.com/pagopa/template-java-spring-microservice/tree/main/.github/workflows'
url_raw = 'https://raw.githubusercontent.com/pagopa/template-java-spring-microservice/main/'

response = requests.get(url)

for item in json.loads(response.text)["payload"]["tree"]["items"]:
    path = item["path"]
    name = item["name"]
    print(name)
    response = requests.get(url_raw+path)
    fo = open(name, "w")
    fo.write(response.text)
    fo.close()


