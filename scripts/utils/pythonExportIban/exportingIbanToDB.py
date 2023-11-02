import csv
import os

import oracledb

connection = oracledb.connect(
    dsn=os.environ['SPRING_DATASOURCE_HOST'],
    port=os.environ['SPRING_DATASOURCE_PORT'],
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()
data = []
with open('./IbanCsv/Iban_output.csv') as csv_file:
    data = list(csv.reader(csv_file))
cursor.executemany("""
        INSERT INTO NODO4_CFG.IBAN (IBAN, FISCAL_CODE, DESCRIPTION, DUE_DATE)
        VALUES (:1, :2, :3, to_timestamp(:4,'YYYY-MM-DD HH24:MI:SS.FF6'))""", data)
connection.commit()
cursor.close()
connection.close()
