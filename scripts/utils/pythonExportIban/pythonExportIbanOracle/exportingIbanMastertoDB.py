import jaydebeapi
import csv

connection  = jaydebeapi.connect(
    "oracle.jdbc.driver.OracleDriver",
    "placeholder_url",
    ["placeholder_username", "placeholder_password"],
    "placeholder_driver_path")
cursor = connection.cursor()
obj_id_iban = 2063400
with open('path/to/output/ibanmaster/csv/file.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    next(csv_reader)
    for row in csv_reader:
        print(row)
        cursor.execute(f"INSERT INTO NODO4_CFG.iban_master (inserted_date, validity_date, state, fk_pa, fk_iban, description) VALUES (to_timestamp('{row[4]}','YYYY-MM-DD HH24:MI:SS.FF6'), to_timestamp('{row[5]}','YYYY-MM-DD HH24:MI:SS.FF6'), '{row[3]}', (Select obj_id from NODO4_CFG.pa where id_dominio = '{row[1]}'), (Select obj_id from NODO4_CFG.iban where iban = '{row[2]}'), '{row[6]}')")
# Prima di avviare controlla che esistano tutti gli id_dominio delle pa
print("Fatto")

cursor.close()
connection.close()
