#!/bin/bash

echo "Rollback all"

python3 rollback.py

echo "Deleted all record in tables IBAN, IBAN_MASTER, IBAN_ATTRIBUTES and IBAN_ATTRIBUTES_MASTER"



