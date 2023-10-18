import csv
import os

import oracledb

connection = oracledb.connect(
    dsn="db-nodo-pagamenti.d.db-nodo-pagamenti.com/NDPSPCT_PP_NODO4_CFG",
    port=1522,
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()

with open('./IbanCsv/IbanView.csv', 'r', newline='') as source, open('./IbanCsv/Iban_Master_output.csv', 'w', newline='') as result:
    csvreader = csv.reader(source, delimiter=',')
    csvwriter = csv.writer(result, delimiter=',')

    csvwriter.writerow(['denominazioneEnte','codiceFiscale','iban','stato','dataAttivazioneIban','dataPubblicazioneIban','descrizione'])
    next(csvreader)
    # Process data rows
    for row in csvreader:
        cursor.execute(f"Select id_dominio from NODO4_CFG.pa where obj_id={row[0]}")
        result_set = cursor.fetchall()
        if(result_set == ''):
            print(f"Problem with {row[1]}")
        rowToWrite = [row[4], result_set[0][0], row[1], "ENABLED", row[2], row[3], row[4]]
        csvwriter.writerow(rowToWrite)

cursor.close()
connection.close()
