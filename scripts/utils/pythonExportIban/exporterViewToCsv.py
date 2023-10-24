import numpy as np
import os

import oracledb

connection = oracledb.connect(
    dsn=os.environ['SPRING_DATASOURCE_HOST'],
    port=os.environ['SPRING_DATASOURCE_PORT'],
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
