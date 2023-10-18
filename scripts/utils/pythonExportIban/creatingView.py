import os

import oracledb

connection = oracledb.connect(
    dsn="db-nodo-pagamenti.d.db-nodo-pagamenti.com/NDPSPCT_PP_NODO4_CFG",
    port=1522,
    user=os.environ['SPRING_DATASOURCE_USERNAME'],
    password=os.environ['SPRING_DATASOURCE_PASSWORD']
)
cursor = connection.cursor()

cursor.execute("RENAME IBAN_VALIDI_PER_PA to IBAN_VALIDI_PER_PA_ORIG")

sqlCommand = """
CREATE OR REPLACE VIEW IBAN_VALIDI_PER_PA AS
	   SELECT mas.fk_pa,
       det.iban          AS iban_accredito,
       mas.validity_date AS data_inizio_validita,
       mas.INSERTED_DATE AS data_pubblicazione,
       -- NULL AS data_pubblicazione,
       p.ID_DOMINIO   	 AS ragione_sociale,
       NULL              AS id_merchant,
       'NA'              AS id_banca_seller,
       NULL              AS chiave_avvio,
       NULL              AS chiave_esito,
       mas.OBJ_ID 		 AS obj_id,
       mas.obj_id        AS master_obj
	FROM   ((iban_master mas
         JOIN (
         SELECT iban_master.obj_id AS obj_id
               FROM   iban_master
               WHERE  ( iban_master.validity_date <= current_timestamp )
               ) right_id_by_pk ON (( mas.obj_id = right_id_by_pk.obj_id )))
        JOIN iban det
          ON (( mas.fk_iban = det.obj_id ))
        JOIN PA p ON (( mas.FK_PA = p.OBJ_ID)) 
          )
"""

cursor.execute(sqlCommand)
cursor.execute("DROP VIEW IBAN_VALIDI_PER_PA_NEW")

cursor.close()
connection.close()
