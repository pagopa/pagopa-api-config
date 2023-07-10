import csv
from datetime import datetime

with open('/Users/federico.ruzzier/Desktop/IbanCsv/export_IBAN_20230626.csv', 'r', newline='') as source, open('/Users/federico.ruzzier/Desktop/IbanCsv/export_IBAN_master_output.csv', 'w', newline='') as result:
    csvreader = csv.reader(source, delimiter=';')
    csvwriter = csv.writer(result, delimiter=';')

    csvwriter.writerow(['denominazioneEnte','codiceFiscale','codAmm','iban','idSellerBank','stato','dataAttivazioneIban','descrizione;etichetta'])
    next(csvreader)
    # Process data rows
    for row in csvreader:
        new_date = ''
        if(row[6] != ''):
            d = datetime.strptime(row[6], '%d/%m/%Y')
            new_date = d.strftime('%m-%d-%Y')
        rowToWrite = [row[0], row[1], row[2], row[3], row[4], row[5], new_date, row[7], row[8]]
        csvwriter.writerow(rowToWrite)