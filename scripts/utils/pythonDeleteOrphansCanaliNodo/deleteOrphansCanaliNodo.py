import os

import oracledb

connection = oracledb.connect(
    dsn=os.environ['SPRING_DATASOURCE_HOST'],
    port=os.environ['SPRING_DATASOURCE_PORT'],
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()
cursor.execute("DELETE FROM NODO4_CFG.CANALI_NODO cn WHERE cn.OBJ_ID IN (SELECT OBJ_ID FROM NODO4_CFG.CANALI_NODO cn WHERE cn.OBJ_ID NOT IN (SELECT OBJ_ID FROM NODO4_CFG.CANALI_NODO cn WHERE cn.OBJ_ID IN (SELECT FK_CANALI_NODO FROM NODO4_CFG.CANALI c )))")
connection.commit()
cursor.close()
connection.close()
