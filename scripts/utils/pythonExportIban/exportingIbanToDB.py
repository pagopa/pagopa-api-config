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
data = []
with open('./IbanCsv/Iban_output.csv') as csv_file:
    data = list(csv.reader(csvfile))
print(data)
cursor.executemany("""
        INSERT INTO NODO4_CFG.IBAN (IBAN, FISCAL_CODE, DESCRIPTION, DUE_DATE)
        VALUES (':1', ':2', ':3', to_timestamp(':4','YYYY-MM-DD HH24:MI:SS.FF6'))""", data)
connection.commit()
cursor.close()
connection.close()
