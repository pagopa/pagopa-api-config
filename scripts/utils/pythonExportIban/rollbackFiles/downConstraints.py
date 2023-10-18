import os

import oracledb

connection = oracledb.connect(
    dsn="db-nodo-pagamenti.d.db-nodo-pagamenti.com/NDPSPCT_PP_NODO4_CFG",
    port=1522,
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()
print("Rollback IBAN_ATTRIBUTES")
cursor.execute("ALTER TABLE IBAN_ATTRIBUTES_MASTER DROP CONSTRAINT FK_IBAN_ATTRIBUTES_MASTER_TO_IBAN_ATTRIBUTES")
cursor.execute("ALTER TABLE IBAN_ATTRIBUTES_MASTER DROP CONSTRAINT FK_IBAN_ATTRIBUTES_MASTER_TO_IBAN_MASTER")
cursor.execute("ALTER TABLE IBAN_MASTER DROP CONSTRAINT FK_IBAN_MASTER_TO_IBAN")
cursor.close()
connection.close()
