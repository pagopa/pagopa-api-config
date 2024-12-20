import os
import oracledb

connection = oracledb.connect(
    dsn=os.environ['SPRING_DATASOURCE_HOST'],
    port=os.environ['SPRING_DATASOURCE_PORT'],
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()

cursor.execute("UPDATE iban SET fk_pa = t.ext_pa FROM (SELECT master.fk_pa as ext_pa, master.fk_iban, MIN(master.inserted_date)	FROM iban ib JOIN iban_master master ON master.fk_iban = ib.obj_id GROUP BY master.fk_pa, master.fk_iban) t WHERE obj_id = t.fk_iban AND fk_pa IS NULL")
connection.commit()

cursor.close()
connection.close()

