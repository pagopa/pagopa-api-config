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
cursor.execute("ALTER TABLE \"NODO4_CFG\".\"IBAN_ATTRIBUTES_MASTER\" ADD CONSTRAINT \"FK_IBAN_ATTRIBUTES_MASTER_TO_IBAN_ATTRIBUTES\" FOREIGN KEY (\"FK_IBAN_ATTRIBUTE\") REFERENCES \"NODO4_CFG\".\"IBAN_ATTRIBUTES\" (\"OBJ_ID\") ENABLE")
cursor.execute("ALTER TABLE \"NODO4_CFG\".\"IBAN_ATTRIBUTES_MASTER\" ADD CONSTRAINT \"FK_IBAN_ATTRIBUTES_MASTER_TO_IBAN_MASTER\" FOREIGN KEY (\"FK_IBAN_MASTER\") REFERENCES \"NODO4_CFG\".\"IBAN_MASTER\" (\"OBJ_ID\") ENABLE")
cursor.execute("ALTER TABLE \"NODO4_CFG\".\"IBAN_MASTER\" ADD CONSTRAINT \"FK_IBAN_MASTER_TO_IBAN\" FOREIGN KEY (\"FK_IBAN\") REFERENCES \"NODO4_CFG\".\"IBAN\" (\"OBJ_ID\") ENABLE")
cursor.close()
connection.close()
