import jaydebeapi
import csv
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

cursor.execute(f"INSERT INTO NODO4_CFG.iban_attributes (ATTRIBUTE_NAME, ATTRIBUTE_DESCRIPTION) VALUES ('0201138TS', 'Canone Unico Patrimoniale - CORPORATE (0201138TS)')")
cursor.execute(f"Select obj_id from NODO4_CFG.iban_attributes where ATTRIBUTE_NAME = '0201138TS'")
iban_attribute = cursor.fetchall()

with open(sys.argv[7]) as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=';')
    next(csv_reader)
    for row in csv_reader:
        cursor.execute(f"Select obj_id from NODO4_CFG.iban_master where fk_pa=(Select obj_id from NODO4_CFG.pa where id_Dominio='{row[1]}') and fk_iban=(Select obj_id from NODO4_CFG.iban where iban='{row[3]}')")
        iban_master_element = cursor.fetchall()
        if(len(iban_master_element) == 1 and row[8] == '0201138TS'):
            cursor.execute(f"INSERT INTO NODO4_CFG.iban_attributes_master (FK_IBAN_MASTER, FK_IBAN_ATTRIBUTE) VALUES ('{iban_master_element[0][0]}', '{iban_attribute[0][0]}')")


cursor.close()
connection.close()

