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
        INSERT INTO NODO4_CFG.iban_master (inserted_date, validity_date, state, fk_pa, fk_iban, description)
        VALUES (to_timestamp(:6,'YYYY-MM-DD HH24:MI:SS'), to_timestamp(:5,'YYYY-MM-DD HH24:MI:SS'), :4, (Select obj_id from NODO4_CFG.pa where id_dominio = :2), (Select obj_id from NODO4_CFG.iban where iban = :3), :7)""", data)
connection.commit()
cursor.close()
connection.close()
