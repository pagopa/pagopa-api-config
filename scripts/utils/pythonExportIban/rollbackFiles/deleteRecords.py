import os

import oracledb

connection = oracledb.connect(
    dsn=os.environ['SPRING_DATASOURCE_HOST'],
    port=os.environ['SPRING_DATASOURCE_PORT'],
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()
print("Rollback IBAN_ATTRIBUTES_MASTER")
cursor.execute("TRUNCATE TABLE NODO4_CFG.IBAN_ATTRIBUTES_MASTER")
print("Rollback IBAN_ATTRIBUTES")
cursor.execute("TRUNCATE TABLE NODO4_CFG.IBAN_ATTRIBUTES")
print("Rollback IBAN_MASTER")
cursor.execute("TRUNCATE TABLE NODO4_CFG.IBAN_MASTER")
print("Rollback IBAN")
cursor.execute("TRUNCATE TABLE NODO4_CFG.IBAN")
connection.commit()
cursor.close()
connection.close()
