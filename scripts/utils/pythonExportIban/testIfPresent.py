import jaydebeapi
import csv

connection  = jaydebeapi.connect(
    "placeholder",
    "placeholder",
    ["placeholder", "placeholder"],
    "placeholder")
cursor = connection.cursor()
counter = 0
with open('/Users/federico.ruzzier/Desktop/IbanCsv/export_IBAN_output.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=';')
    next(csv_reader)
    for row in csv_reader:
        cursor.execute(f"Select * from cfg.pa p where p.id_dominio = '{row[1]}'")
        result_set = cursor.fetchall()
        if(result_set == []):
            counter += 1
            #print(counter)
            print(f"{row[0]};{row[1]};{row[2]};{row[3]}")
#Prima di avviare controlla che esistano tutti gli id_dominio delle pa
print(counter)
