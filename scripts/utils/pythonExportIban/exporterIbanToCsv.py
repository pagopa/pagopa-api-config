import csv
from datetime import datetime

thisdictDate = {}
thisdictPa = {}
entries = []
duplicate_entries = []
with open('./IbanCsv/Iban_Master_output.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    line_count = 0
    for row in csv_reader:
        print(row[2])
        if line_count == 0:
            print(f'Column names are {", ".join(row)}')
            line_count += 1
        else:
            if(thisdictDate.get(row[2]) != None):
                print(row[2])
                if(datetime.strptime(thisdictDate.get(row[2]), "%Y-%m-%d %H:%M:%S.%f") > datetime.strptime((row[4]), "%Y-%m-%d %H:%M:%S.%f")):
                    thisdictDate.update({row[2]: row[4]})
                    thisdictPa.update({row[2]: row[1]})
            else:
                thisdictDate.update({row[2]: row[4]})
                thisdictPa.update({row[2]: row[1]})

with open('./IbanCsv/Iban_Master_output.csv', 'r', newline='') as source, open('./IbanCsv/Iban_output.csv', 'w', newline='') as result:
    csvreader = csv.reader(source, delimiter=',')
    csvwriter = csv.writer(result, delimiter=',')

    csvwriter.writerow(['iban', 'fiscal_code', 'description', 'publication_date'])
    next(csvreader)
    # Process data rows
    for row in csvreader:
        new_date = ''
        if(row[4] != ''):
            d = datetime.strptime(thisdictDate.get(row[2]), "%Y-%m-%d %H:%M:%S.%f")
            new_date = d.strftime("%Y-%m-%d %H:%M:%S.%f")
        if(thisdictPa.get(row[2]) != None):
            rowToWrite = [row[2], thisdictPa.get(row[2]), row[0], new_date]
            thisdictPa.pop(row[2], None)
            csvwriter.writerow(rowToWrite)