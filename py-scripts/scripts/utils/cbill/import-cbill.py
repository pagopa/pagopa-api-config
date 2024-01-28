import oracledb
import sys
import csv
import os
import time

# load cbill export
dirname = os.path.dirname(__file__)
file_path = dirname + "/ListaXPagoPA_19gen.csv"
print("import-cbill|INFO|loading file [{}]".format(file_path))
filename = open(file_path, 'r')
file = csv.reader(filename, delimiter=';')

print("import-cbill|INFO|file loaded")
print("import-cbill|INFO|skipping header row")
next(file, None)

try:
    print("import-cbill|INFO|creating db connection")
    connection = oracledb.connect(
        dsn=os.environ['SPRING_DATASOURCE_HOST'],
        port=int(os.environ['SPRING_DATASOURCE_PORT']),
        user=os.environ['SPRING_DATASOURCE_USERNAME'],
        password=os.environ['SPRING_DATASOURCE_PASSWORD']
    )
    print("import-cbill|INFO|connection established")

    print("import-cbill|INFO|cycling over file")
    counter = 0
    for row in file:

        id_dominio = row[0]
        cbill = row[1]

        if id_dominio and cbill:

            #print("import-cbill|INFO|id_dominio[{}]".format(id_dominio))
            #print("import-cbill|INFO|cbill[{}]".format(cbill))
            sql_statement = """
                    update NODO4_CFG.PA 
                    set CBILL = '{}' 
                    where ID_DOMINIO = '{}'""".format(cbill, id_dominio)
            #print("import-cbill|INFO|sql statement [{}]".format(sql_statement))

            #print("import-cbill|INFO|creating cursor")
            cursor = connection.cursor()

            #print("import-cbill|INFO|executing sql statement")
            cursor.execute(sql_statement, [])

            #print("import-cbill|INFO|ccommitting statement")
            connection.commit()

            #print("import-cbill|INFO|closing cursor")
            cursor.close()

        time.sleep(10/1000)
        if counter % 1000 == 0:
            print("import-cbill|INFO|[{}] record processed".format(str(counter)))
        counter = counter + 1
        #if counter == 10:
        #    break
    print("import-cbill|INFO|closing connection")
    connection.close()
    print("import-cbill|INFO|connection closed")

except Exception as err:
    print(f'import-cbill|ERROR|Generic error occurred: {err}')