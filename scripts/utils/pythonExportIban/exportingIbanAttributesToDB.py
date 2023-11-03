import os
import oracledb

connection = oracledb.connect(
    dsn=os.environ['SPRING_DATASOURCE_HOST'],
    port=os.environ['SPRING_DATASOURCE_PORT'],
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()

cursor.execute("INSERT INTO NODO4_CFG.iban_attributes (ATTRIBUTE_NAME, ATTRIBUTE_DESCRIPTION) VALUES ('0201138TS', 'Canone Unico Patrimoniale - CORPORATE (0201138TS)')")
connection.commit()

cursor.close()
connection.close()

