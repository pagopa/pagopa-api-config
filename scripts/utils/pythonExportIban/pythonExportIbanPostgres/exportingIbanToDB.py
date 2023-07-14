import jaydebeapi
import csv

connection  = jaydebeapi.connect(
    "org.postgresql.Driver",
    "placeholder_url",
    ["placeholder_username", "placeholder_password"],
    "placeholder_driver_path")
cursor = connection.cursor()

with open('path/to/output/iban/csv/file.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    next(csv_reader)
    for row in csv_reader:
        print(row)
        cursor.execute(f"INSERT INTO cfg.iban (iban, fiscal_code, description, due_date) VALUES ('{row[0]}', '{row[1]}', '{row[2]}', '{row[3]}');")
cursor.execute("Select count(*) from cfg.iban")
result_set = cursor.fetchall()
print("Fatto")

cursor.close()
connection.close()
