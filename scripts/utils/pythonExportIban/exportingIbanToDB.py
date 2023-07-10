import jaydebeapi
import csv

connection  = jaydebeapi.connect(
    "placeholder",
    "placeholder",
    ["placeholder", "placeholder"],
    "placeholder")
cursor = connection.cursor()

with open('/Users/federico.ruzzier/Desktop/IbanCsv/export_IBAN_output.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=';')
    next(csv_reader)
    for row in csv_reader:
        print(row)
        cursor.execute(f"INSERT INTO cfg.iban (iban, fiscal_code, description, due_date) VALUES ('{row[0]}', '{row[1]}', '{row[2]}', '{row[3]}');")
cursor.execute("Select count(*) from cfg.iban")
result_set = cursor.fetchall()

cursor.close()
connection.close()
