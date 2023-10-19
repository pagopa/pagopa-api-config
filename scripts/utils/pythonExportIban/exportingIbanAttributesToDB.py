import os
import csv
import oracledb
import sys

connection = oracledb.connect(
    dsn="db-nodo-pagamenti.d.db-nodo-pagamenti.com/NDPSPCT_PP_NODO4_CFG",
    port=1522,
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()

cursor.execute(f"INSERT INTO NODO4_CFG.iban_attributes (ATTRIBUTE_NAME, ATTRIBUTE_DESCRIPTION) VALUES ('0201138TS', 'Canone Unico Patrimoniale - CORPORATE (0201138TS)')")
connection.commit()
cursor.execute(f"Select obj_id from NODO4_CFG.iban_attributes where ATTRIBUTE_NAME = '0201138TS'")
iban_attribute = cursor.fetchall()

data = []
with open(sys.argv[1]) as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=';')
    next(csv_reader)
    for row in csv_reader:
        cursor.execute("""
        Select obj_id from NODO4_CFG.iban_master where fk_pa=(Select obj_id from NODO4_CFG.pa where id_Dominio=:id_dominio) and fk_iban=(Select obj_id from NODO4_CFG.iban where iban=:iban)
        """, [row[1], row[3]])
        iban_master_element = cursor.fetchall()
        if(len(iban_master_element) == 1 and row[8] == '0201138TS'):
            data.append([iban_master_element[0][0], iban_attribute[0][0]])

cursor.executemany("INSERT INTO NODO4_CFG.iban_attributes_master (FK_IBAN_MASTER, FK_IBAN_ATTRIBUTE) VALUES (:1, :2)", data)

cursor.close()
connection.close()

