import csv
import os
import sys

import oracledb

dictDescriptionIbanPa = {}
with open(sys.argv[1]) as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=';')
    for row in csv_reader:
        dictDescriptionIbanPa.update({(row[1], row[3]): row[7]})

connection = oracledb.connect(
    dsn=os.environ['SPRING_DATASOURCE_HOST'],
    port=os.environ['SPRING_DATASOURCE_PORT'],
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()

with open('./IbanCsv/IbanView.csv', 'r', newline='') as source, open('./IbanCsv/Iban_Master_output.csv', 'w', newline='') as result:
    csvreader = csv.reader(source, delimiter=',')
    csvwriter = csv.writer(result, delimiter=',')
    # Process data rows
    for row in csvreader:
        cursor.execute("""
        Select id_dominio from NODO4_CFG.pa where obj_id=:obj_id
        """, obj_id=row[0])
        result_set = cursor.fetchall()
        if(result_set == ''):
            print(f"Problem with {row[1]}")
            continue
        rowToWrite = [dictDescriptionIbanPa.get((result_set[0][0], row[1])), result_set[0][0], row[1], "N/A", row[2], row[3]]
        csvwriter.writerow(rowToWrite)

cursor.close()
connection.close()
