import xml.etree.ElementTree as ET
import sys
import datetime
from datetime import timedelta
import json
import zipfile
import os
import shutil

def icaToIban(icaFile):
    output = {}
    tree = ET.parse(icaFile)
    root = tree.getroot()
    creditor_institution_code = root.find('./identificativoDominio').text
    master_description = ''
    output['creditor_institution_code'] = creditor_institution_code
    output['description'] = master_description
    ibans = []
    iban_description = root.find('./ragioneSociale').text
    validity_date = root.find('./dataInizioValidita').text
    due_date = datetime.datetime.now() + timedelta(days=365)
    for child in root.find('./contiDiAccredito'):
        iban_object = {}
        if(child.tag == 'infoContoDiAccreditoPair'):
            iban_object['iban'] = child.find('./ibanAccredito').text
        elif(child.tag == 'ibanAccredito'):
            iban_object['iban'] = child.text
        iban_object['description'] = iban_description
        iban_object['validity_date'] = validity_date
        iban_object['due_date'] = due_date.isoformat(timespec="seconds")
        ibans.append(iban_object)
    output['ibans'] = ibans
    json_output = json.dumps(output)
    return json_output

folder_name = sys.argv[1].rstrip('.zip') + '_iban'
if(os.path.isdir(folder_name)):
    os.rmdir(folder_name)
os.mkdir(folder_name)
zf = zipfile.ZipFile(sys.argv[1], 'r')
for name in zf.namelist():
    f = zf.open(name)
    if(f.name.startswith('__MACOSX')):
        continue
    out = icaToIban(f)
    completeName = os.path.join(folder_name, f.name.rstrip('.xml') + ".json")
    fileToSave = open(completeName, "w")
    fileToSave.write(out)
    fileToSave.close()
shutil.make_archive(folder_name, 'zip', folder_name)
