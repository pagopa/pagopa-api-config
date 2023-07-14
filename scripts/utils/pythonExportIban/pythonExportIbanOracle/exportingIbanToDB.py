import jaydebeapi
import csv

connection  = jaydebeapi.connect(
    "oracle.jdbc.driver.OracleDriver",
    "placeholder_url",
    ["placeholder_username", "placeholder_password"],
    "placeholder_driver_path")
cursor = connection.cursor()

with open('path/to/output/iban/csv/file.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    next(csv_reader)
    for row in csv_reader:
        print(row)
        cursor.execute("Select count(*) from NODO4_CFG.iban")
        print(cursor.fetchall())
        cursor.execute(f"INSERT INTO NODO4_CFG.IBAN (IBAN, FISCAL_CODE, DESCRIPTION, DUE_DATE) VALUES ('{row[0]}', '{row[1]}', '{row[2]}', to_timestamp('{row[3]}','YYYY-MM-DD HH24:MI:SS.FF6'))")
cursor.execute("Select count(*) from NODO4_CFG.iban")
result_set = cursor.fetchall()

cursor.close()
connection.close()
