import os

import oracledb

connection = oracledb.connect(
    dsn="db-nodo-pagamenti.d.db-nodo-pagamenti.com/NDPSPCT_PP_NODO4_CFG",
    port=1522,
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()
cursor.execute("RENAME IBAN_VALIDI_PER_PA to IBAN_VALIDI_PER_PA_NEW")
cursor.execute("RENAME IBAN_VALIDI_PER_PA_ORIG to IBAN_VALIDI_PER_PA")
cursor.close()
connection.close()
