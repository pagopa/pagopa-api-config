import numpy as np
import os

import oracledb

connection = oracledb.connect(
    dsn="db-nodo-pagamenti.d.db-nodo-pagamenti.com/NDPSPCT_PP_NODO4_CFG",
    port=1522,
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()

cursor.execute("Select * from NODO4_CFG.IBAN_VALIDI_PER_PA")
result_set = cursor.fetchall()
a = np.asarray(result_set)
np.savetxt('./IbanCsv/IbanView.csv', a, delimiter=',', fmt='%s')

cursor.close()
connection.close()
