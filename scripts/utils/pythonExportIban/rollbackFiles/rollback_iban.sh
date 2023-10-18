#!/bin/bash

echo "Rollback view"

python3 rollBackView.py

echo "Table constraints down"

python3 downConstraints.py

echo "deleting records"

python3 deleteRecords.py

echo "Table constraints up"

python3 upConstraints.py

echo "Deleted view, all record in tables IBAN, IBAN_MASTER, IBAN_ATTRIBUTES and IBAN_ATTRIBUTES_MASTER"



