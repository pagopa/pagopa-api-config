import jaydebeapi
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
cursor.execute("TRUNCATE TABLE NODO4_CFG.IBAN_MASTER")
cursor.execute("TRUNCATE TABLE NODO4_CFG.IBAN")
cursor.close()
connection.close()
