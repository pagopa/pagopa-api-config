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
        if(row[7] == '0201138TS'):
            print(row)
            cursor.execute(f"INSERT INTO cfg.iban_attributes_master (fk_iban_master, fk_iban_attribute) VALUES ((Select obj_id from cfg.iban_master where fk_iban = (select obj_id from cfg.iban where iban = '{row[3]}') and fk_pa = (select obj_id from cfg.pa where id_dominio = '{row[1]}')), 2000001);")
# Prima di avviare controlla che esistano tutti gli id_dominio delle pa
print("Fatto")
cursor.close()
connection.close()
