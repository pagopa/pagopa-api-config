import jaydebeapi
import numpy as np
import sys

host = "jdbc:oracle:thin:@" + sys.argv[1] + ":" + sys.argv[2] + "/" + sys.argv[3]
username = sys.argv[4]
password = sys.argv[5]
driver = sys.argv[6]
connection  = jaydebeapi.connect(
    "oracle.jdbc.driver.OracleDriver",
    host,
    [username, password],
    driver)
cursor = connection.cursor()

cursor.execute("Select * from NODO4_CFG.IBAN_VALIDI_PER_PA")
result_set = cursor.fetchall()
a = np.asarray(result_set)
row_to_be_added = np.array(['FK_PA', 'IBAN_ACCREDITO', 'DATA_INIZIO_VALIDITA', 'DATA_PUBBLICAZIONE', 'RAGIONE_SOCIALE', 'ID_MERCHANT', 'ID_BANCA_SELLER', 'CHIAVE_AVVIO', 'CHIAVE_ESITO', 'OBJ_ID', 'MASTER_OBJ'])
a = np.r_[[row_to_be_added], a]
np.savetxt('./IbanCsv/IbanView.csv', a, delimiter=',', fmt='%s')

cursor.close()
connection.close()
