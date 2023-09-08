#!/bin/bash

python3 exporterViewToCsv.py

echo "Original data from existing view exported"

sed -i '' "s/\'//g" ./IbanCsv/IbanView.csv

echo "All ' eliminated"

python3 exporterIbanMasterToCsv.py

python3 exporterIbanToCsv.py

echo "Pre process done"

python3 exportingIbanToDB.py

echo "Populated Iban table"

python3 exportingIbanMastertoDB.py

echo "Populated Iban Master table, goodbye"



