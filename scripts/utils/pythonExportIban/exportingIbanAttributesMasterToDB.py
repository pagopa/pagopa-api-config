import os
import csv
import oracledb
import sys

connection = oracledb.connect(
    dsn=os.environ['SPRING_DATASOURCE_HOST'],
    port=os.environ['SPRING_DATASOURCE_PORT'],
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()

cursor.execute(f"Select obj_id from NODO4_CFG.iban_attributes where ATTRIBUTE_NAME = '0201138TS'")
iban_attribute = cursor.fetchall()

data = []
with open(sys.argv[1]) as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=';')
    for row in csv_reader:
        cursor.execute("""
        Select obj_id from NODO4_CFG.iban_master where fk_pa=(Select obj_id from NODO4_CFG.pa where id_Dominio=:id_dominio) and fk_iban=(Select obj_id from NODO4_CFG.iban where iban=:iban)
        """, id_dominio=row[1], iban=row[3])
        iban_master_element = cursor.fetchall()
        if(len(iban_master_element) == 1 and row[8] == '0201138TS'):
            data.append([iban_master_element[0][0], iban_attribute[0][0]])

cursor.executemany("INSERT INTO NODO4_CFG.iban_attributes_master (FK_IBAN_MASTER, FK_IBAN_ATTRIBUTE) VALUES (:1, :2)", data)
connection.commit()

cursor.close()
connection.close()

