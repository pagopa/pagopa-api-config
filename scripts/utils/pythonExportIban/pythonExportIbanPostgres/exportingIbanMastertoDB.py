import jaydebeapi
import csv

connection  = jaydebeapi.connect(
    "org.postgresql.Driver",
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
        cursor.execute(f"INSERT INTO cfg.iban_master (inserted_date, validity_date, state, fk_pa, fk_iban, description) VALUES ('{row[5]}+01', '{row[4]}', '{row[3]}', (Select obj_id from cfg.pa where id_dominio = '{row[1]}'), (Select obj_id from cfg.iban where iban = '{row[2]}'), '{row[6]}');")
# Prima di avviare controlla che esistano tutti gli id_dominio delle pa
print("Fatto")

cursor.close()
connection.close()
