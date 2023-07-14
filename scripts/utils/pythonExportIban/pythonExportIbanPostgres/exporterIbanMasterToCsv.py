import csv
from datetime import datetime
import jaydebeapi

connection  = jaydebeapi.connect(
    "org.postgresql.Driver",
    "placeholder_url",
    ["placeholder_username", "placeholder_password"],
    "placeholder_driver_path")
cursor = connection.cursor()

with open('path/to/input/csv/file.csv', 'r', newline='') as source, open('path/to/output/ibanmaster/csv/file.csv', 'w', newline='') as result:
    csvreader = csv.reader(source, delimiter=',')
    csvwriter = csv.writer(result, delimiter=',')

    csvwriter.writerow(['denominazioneEnte','codiceFiscale','iban','stato','dataAttivazioneIban','dataPubblicazioneIban','descrizione','etichetta'])
    next(csvreader)
    # Process data rows
    for row in csvreader:
        cursor.execute(f"Select id_dominio from cfg.pa where obj_id={row[0]}")
        result_set = cursor.fetchall()
        if(result_set == ''):
            print(f"{row} does not have EC")
        rowToWrite = [row[4], result_set[0][0], row[1], "ENABLED", row[2], row[3], row[4],]
        csvwriter.writerow(rowToWrite)
