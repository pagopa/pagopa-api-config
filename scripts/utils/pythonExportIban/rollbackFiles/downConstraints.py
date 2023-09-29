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
print("Rollback IBAN_ATTRIBUTES")
cursor.execute("ALTER TABLE IBAN_ATTRIBUTES_MASTER DROP CONSTRAINT FK_IBAN_ATTRIBUTES_MASTER_TO_IBAN_ATTRIBUTES")
cursor.execute("ALTER TABLE IBAN_ATTRIBUTES_MASTER DROP CONSTRAINT FK_IBAN_ATTRIBUTES_MASTER_TO_IBAN_MASTER")
cursor.execute("ALTER TABLE IBAN_MASTER DROP CONSTRAINT FK_IBAN_MASTER_TO_IBAN")
cursor.close()
connection.close()
