import os

import oracledb

connection = oracledb.connect(
    dsn="db-nodo-pagamenti.d.db-nodo-pagamenti.com/NDPSPCT_PP_NODO4_CFG",
    port=1522,
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()
cursor.execute("ALTER TABLE \"NODO4_CFG\".\"IBAN_ATTRIBUTES_MASTER\" ADD CONSTRAINT \"FK_IBAN_ATTRIBUTES_MASTER_TO_IBAN_ATTRIBUTES\" FOREIGN KEY (\"FK_IBAN_ATTRIBUTE\") REFERENCES \"NODO4_CFG\".\"IBAN_ATTRIBUTES\" (\"OBJ_ID\") ENABLE")
cursor.execute("ALTER TABLE \"NODO4_CFG\".\"IBAN_ATTRIBUTES_MASTER\" ADD CONSTRAINT \"FK_IBAN_ATTRIBUTES_MASTER_TO_IBAN_MASTER\" FOREIGN KEY (\"FK_IBAN_MASTER\") REFERENCES \"NODO4_CFG\".\"IBAN_MASTER\" (\"OBJ_ID\") ENABLE")
cursor.execute("ALTER TABLE \"NODO4_CFG\".\"IBAN_MASTER\" ADD CONSTRAINT \"FK_IBAN_MASTER_TO_IBAN\" FOREIGN KEY (\"FK_IBAN\") REFERENCES \"NODO4_CFG\".\"IBAN\" (\"OBJ_ID\") ENABLE")
cursor.close()
connection.close()
