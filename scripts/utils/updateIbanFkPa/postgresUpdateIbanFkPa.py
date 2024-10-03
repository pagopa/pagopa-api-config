import os
import psycopg2

sql_statement = 'UPDATE iban SET fk_pa = t.ext_pa FROM (SELECT master.fk_pa as ext_pa, master.fk_iban, MIN(master.inserted_date)	FROM iban ib JOIN iban_master master ON master.fk_iban = ib.obj_id GROUP BY master.fk_pa, master.fk_iban) t WHERE obj_id = t.fk_iban AND fk_pa IS NULL'
connection = oracledb.connect(
    database=os.environ['SPRING_DATASOURCE_DB']
    host=os.environ['SPRING_DATASOURCE_HOST'],
    port=os.environ['SPRING_DATASOURCE_PORT'],
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()
cursor.execute(sql_statement)
connection.commit()

cursor.close()
connection.close()
