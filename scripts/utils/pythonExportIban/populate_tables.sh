#!/bin/bash

PDA_CSV_FILE=$1

echo "Create file where View will be saved"

mkdir ./IbanCsv #crea la cartella per i file di pre process

touch ./IbanCsv/IbanView.csv #crea il file csv con il contenuto della vista

python3 exporterViewToCsv.py #esporta la vista all'interno del file appena creato

echo "Original data from existing view exported"

python3 exporterIbanMasterToCsv.py $PDA_CSV_FILE #preparazione del file csv iban master

python3 exporterIbanToCsv.py #preparazione del file csv per iban

echo "Pre process done"

python3 exportingIbanToDB.py # popolamento tabella iban

echo "Populated Iban table"

python3 exportingIbanMastertoDB.py # popolamento tabella iban master

echo "Populated Iban Master table"

python3 exportingIbanAttributesToDB.py # popolamento tabella iban attribute

python3 exportingIbanAttributesMasterToDB.py $PDA_CSV_FILE # popolamento iban attribute master

echo "Populated Iban Attributes and Iban Attributes Master table"



