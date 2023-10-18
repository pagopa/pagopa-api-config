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
with open('./IbanCsv/Iban_Master_output.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    next(csv_reader)
    for row in csv_reader:
        cursor.execute(f"INSERT INTO NODO4_CFG.iban_master (inserted_date, validity_date, state, fk_pa, fk_iban, description) VALUES (to_timestamp('{row[5]}','YYYY-MM-DD HH24:MI:SS'), to_timestamp('{row[4]}','YYYY-MM-DD HH24:MI:SS'), '{row[3]}', (Select obj_id from NODO4_CFG.pa where id_dominio = '{row[1]}'), (Select obj_id from NODO4_CFG.iban where iban = '{row[2]}'), '{row[6]}')")

cursor.close()
connection.close()
