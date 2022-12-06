#!/bin/bash

# set -e

# force delete image and container of MS AZ Cosmos DB Emulator
#docker container rm -f azure-cosmosdb-linux-emulator
#docker image rm -f mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator:latest

PORT=$1
JVM_HOME=$2

# Main ...
if [ -z "$PORT" ]
then
  PORT=8081
  echo "CosmosDB starting on DEFAULT port $PORT"
else
  echo "CosmosDB starting on specific port $PORT"
fi

if [ -z "$JVM_HOME" ]
then
  JVM_HOME=$JAVA_HOME
  echo "Using DEFAULT JAVA_HOME $JVM_HOME"
else
  echo "Using custom JAVA_HOME $JVM_HOME"
fi


# Azure Cosmos DB Emulator
URL="https://localhost:$PORT/_explorer/index.html"

ipaddr=$(ifconfig | grep "inet " | grep -Fv 127.0.0.1 | awk '{print $2}' | head -n 1)
echo "Using ${ipaddr} for CosmosDB configuration..."

docker pull mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator

docker run \
    --detach \
    --publish $PORT:8081 \
    --publish 10251-10254:10251-10254 \
    --memory 3g --cpus=2.0 \
    --name=azure-cosmosdb-linux-emulator \
    --env AZURE_COSMOS_EMULATOR_PARTITION_COUNT=10 \
    --env AZURE_COSMOS_EMULATOR_ENABLE_DATA_PERSISTENCE=true \
    --env AZURE_COSMOS_EMULATOR_IP_ADDRESS_OVERRIDE=$ipaddr \
    --interactive \
    --tty \
    mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator

echo -n "CosmosDB starting..."
cosmos_started=$(docker logs azure-cosmosdb-linux-emulator | grep -wc Started)
# check cosmos is UP
while [ "$cosmos_started" != "12" ]
do
    sleep 3
    echo -n "."
    cosmos_started=$(docker logs azure-cosmosdb-linux-emulator | grep -wc Started)
done

echo "!!! STARTED !!!"

echo "Setting certificate..."

curl -k "https://${ipaddr}:${PORT}/_explorer/emulator.pem" > emulatorcert.crt

# add keychain accesss
sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain emulatorcert.crt

# add jvm trust-store
keystore_alias="cosmoskeystore"
echo "Remember, the keystore passowrd is: changeit"
sudo keytool -delete -alias $keystore_alias -keystore "${JVM_HOME}/lib/security/cacerts"
sudo keytool -trustcacerts -keystore "${JVM_HOME}/lib/security/cacerts" -storepass changeit -importcert -alias $keystore_alias -file emulatorcert.crt

echo "Setting certificate...done."

open $URL
