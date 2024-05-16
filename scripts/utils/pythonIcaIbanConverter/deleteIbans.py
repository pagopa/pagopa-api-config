import oracledb
import sys
import csv
import os

data = []
with open(sys.argv[1]) as csv_file:
    data = list(csv.reader(csv_file))
sql_statement = 'DELETE FROM NODO4_CFG.IBAN_MASTER WHERE FK_PA = (SELECT OBJ_ID FROM NODO4_CFG.PA WHERE ID_DOMINIO = :1)'
connection = oracledb.connect(
    dsn=os.environ['SPRING_DATASOURCE_HOST'],
    port=os.environ['SPRING_DATASOURCE_PORT'],
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()
cursor.executemany(sql_statement, data)
connection.commit()

cursor.close()
connection.close()
