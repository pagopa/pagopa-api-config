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
with open('./IbanCsv/Iban_Master_output.csv') as csv_file:
    data = list(csv.reader(csv_file))
cursor.executemany("""
        INSERT INTO NODO4_CFG.iban_master (description, fk_pa, fk_iban, state, validity_date, inserted_date)
        VALUES (:1, (Select obj_id from NODO4_CFG.pa where id_dominio = :2), (Select obj_id from NODO4_CFG.iban where iban = :3), :4,to_timestamp(:5,'YYYY-MM-DD HH24:MI:SS'), to_timestamp(:6,'YYYY-MM-DD HH24:MI:SS'))""", data)
connection.commit()
cursor.close()
connection.close()
