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

with open('./IbanCsv/Iban_output.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    next(csv_reader)
    for row in csv_reader:
        cursor.execute(f"INSERT INTO NODO4_CFG.IBAN (IBAN, FISCAL_CODE, DESCRIPTION, DUE_DATE) VALUES ('{row[0]}', '{row[1]}', '{row[2]}', to_timestamp('{row[3]}','YYYY-MM-DD HH24:MI:SS.FF6'))")
cursor.close()
connection.close()
