import os
import psycopg2

sql_statement = '"UPDATE iban_master SET due_date = t.due_date FROM (SELECT DISTINCT ib.obj_id, ib.due_date FROM iban ib JOIN iban_master master ON master.fk_iban = ib.obj_id WHERE master.due_date IS NULL) t WHERE fk_iban = t.obj_id"'
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
