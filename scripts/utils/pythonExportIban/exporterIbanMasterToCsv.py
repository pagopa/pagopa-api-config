import csv
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

with open('./IbanCsv/IbanView.csv', 'r', newline='') as source, open('./IbanCsv/Iban_Master_output.csv', 'w', newline='') as result:
    csvreader = csv.reader(source, delimiter=',')
    csvwriter = csv.writer(result, delimiter=',')

    csvwriter.writerow(['denominazioneEnte','codiceFiscale','iban','stato','dataAttivazioneIban','dataPubblicazioneIban','descrizione'])
    next(csvreader)
    # Process data rows
    for row in csvreader:
        cursor.execute(f"Select id_dominio from NODO4_CFG.pa where obj_id={row[0]}")
        result_set = cursor.fetchall()
        if(result_set == ''):
            print(f"Problem with {row[1]}")
        rowToWrite = [row[4], result_set[0][0], row[1], "ENABLED", row[2], row[3], row[4]]
        csvwriter.writerow(rowToWrite)

cursor.close()
connection.close()
