#!/bin/bash

DB_HOST_IPADDRESS=$1
DB_HOST_PORT=$2
DB_HOST_NAME=$3
DB_USERNAME=$4
DB_PASSWORD=$5
DB_DRIVER_PATH=$6

python3 exporterViewToCsv.py DB_HOST_IPADDRESS DB_HOST_PORT DB_HOST_NAME DB_USERNAME DB_PASSWORD DB_DRIVER_PATH

echo "Original data from existing view exported"

sed -i '' "s/\'//g" ./IbanCsv/IbanView.csv

echo "All ' eliminated"

python3 exporterIbanMasterToCsv.py DB_HOST_IPADDRESS DB_HOST_PORT DB_HOST_NAME DB_USERNAME DB_PASSWORD DB_DRIVER_PATH

python3 exporterIbanToCsv.py

echo "Pre process done"

python3 exportingIbanToDB.py DB_HOST_IPADDRESS DB_HOST_PORT DB_HOST_NAME DB_USERNAME DB_PASSWORD DB_DRIVER_PATH

echo "Populated Iban table"

python3 exportingIbanMastertoDB.py DB_HOST_IPADDRESS DB_HOST_PORT DB_HOST_NAME DB_USERNAME DB_PASSWORD DB_DRIVER_PATH

echo "Populated Iban Master table, goodbye"



