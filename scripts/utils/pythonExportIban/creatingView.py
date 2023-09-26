import jaydebeapi
import sys

host = "jdbc:oracle:thin:@" + sys.argv[1] + ":" + sys.argv[2] + "/" + sys.argv[3]
username = sys.argv[4]
password = sys.argv[5]
driver = sys.argv[6]
connection  = jaydebeapi.connect(
    "oracle.jdbc.driver.OracleDriver",
    host,
    [username, password],
    driver)
cursor = connection.cursor()

cursor.execute("RENAME IBAN_VALIDI_PER_PA to IBAN_VALIDI_PER_PA_ORIG")

sqlCommand = """
CREATE OR REPLACE VIEW IBAN_VALIDI_PER_PA AS
	   SELECT mas.fk_pa,
       det.iban          AS iban_accredito,
       mas.validity_date AS data_inizio_validita,
       TO_TIMESTAMP(NULL, 'YYYY-MM-DD HH24:MI:SS') AS data_pubblicazione,
       -- NULL AS data_pubblicazione,
       det.fiscal_code   AS ragione_sociale,
       NULL              AS id_merchant,
       NULL              AS id_banca_seller,
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
          ON (( mas.fk_iban = det.obj_id )))
"""

cursor.execute(sqlCommand)

cursor.close()
connection.close()
