import jaydebeapi
import csv

connection  = jaydebeapi.connect(
    "oracle.jdbc.driver.OracleDriver",
    "placeholder_url",
    ["placeholder_username", "placeholder_password"],
    "placeholder_driver_path")
cursor = connection.cursor()
counter = 0
dict = {}
with open('path/to/output/ibanmaster/csv/file.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    next(csv_reader)
    for row in csv_reader:
        cursor.execute(f"Select * from NODO4_CFG.pa p where p.id_dominio = '{row[1]}'")
        result_set = cursor.fetchall()
        print(f"{row[3]} with {row[1]}: {result_set}")
        if(result_set == []):
            counter += 1
            dict.update({row[1]: "not exist"})
#Prima di avviare controlla che esistano tutti gli id_dominio delle pa
print(counter)
print(dict)
