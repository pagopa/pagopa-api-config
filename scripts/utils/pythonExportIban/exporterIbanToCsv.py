import csv
from datetime import datetime

thisdictDate = {}
thisdictPa = {}
entries = []
duplicate_entries = []
with open('/Users/federico.ruzzier/Desktop/IbanCsv/export_IBAN_20230626.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=';')
    line_count = 0
    for row in csv_reader:
        if line_count == 0:
            print(f'Column names are {", ".join(row)}')
            line_count += 1
        else:
            if(thisdictDate.get(row[3]) != None):
                if(datetime.strptime(thisdictDate.get(row[3]), "%d/%m/%Y").date() > datetime.strptime((row[6]), "%d/%m/%Y").date()):
                    thisdictDate.update({row[3]: row[6]})
                    thisdictPa.update({row[3]: row[1]})
            else:
                thisdictDate.update({row[3]: row[6]})
                thisdictPa.update({row[3]: row[1]})

with open('/Users/federico.ruzzier/Desktop/IbanCsv/export_IBAN_20230626.csv', 'r', newline='') as source, open('/Users/federico.ruzzier/Desktop/IbanCsv/export_IBAN_output.csv', 'w', newline='') as result:
    csvreader = csv.reader(source, delimiter=';')
    csvwriter = csv.writer(result, delimiter=';')

    csvwriter.writerow(['iban', 'fiscal_code', 'description', 'publication_date'])
    next(csvreader)
    # Process data rows
    for row in csvreader:
        new_date = ''
        if(row[6] != ''):
            d = datetime.strptime(thisdictDate.get(row[3]), '%d/%m/%Y')
            new_date = d.strftime('%m-%d-%Y')
        if(thisdictPa.get(row[3]) != None):
            rowToWrite = [row[3], thisdictPa.get(row[3]), row[7], new_date]
            thisdictPa.pop(row[3], None)
            csvwriter.writerow(rowToWrite)