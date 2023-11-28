import xml.etree.ElementTree as ET
import sys
import zipfile
import csv

def addCreditorInstitutionCode(icaFile):
    tree = ET.parse(icaFile)
    root = tree.getroot()
    return root.find('./identificativoDominio').text

toDelete = []
zf = zipfile.ZipFile(sys.argv[1], 'r')
for name in zf.namelist():
    f = zf.open(name)
    toDelete.append([addCreditorInstitutionCode(f)])
with open('./iban_to_delete.csv', 'w', newline='') as result:
    csvwriter = csv.writer(result, delimiter=',')
    csvwriter.writerows(toDelete)
