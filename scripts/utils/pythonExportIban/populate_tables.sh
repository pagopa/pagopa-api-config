#!/bin/bash

PDA_CSV_FILE=$1

echo "Create file where View will be saved"

mkdir ./IbanCsv

touch ./IbanCsv/IbanView.csv

python3 exporterViewToCsv.py

echo "Original data from existing view exported"

python3 exporterIbanMasterToCsv.py $PDA_CSV_FILE

python3 exporterIbanToCsv.py

echo "Pre process done"

python3 exportingIbanToDB.py

echo "Populated Iban table"

python3 exportingIbanMastertoDB.py

echo "Populated Iban Master table"

python3 exportingIbanAttributesToDB.py

python3 exportingIbanAttributesMasterToDB.py $PDA_CSV_FILE

echo "Populated Iban Attributes and Iban Attributes Master table"



