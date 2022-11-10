alter table NODO4_CFG.STAZIONI add INVIO_RT_ISTANTANEO CHAR default 'N' not null;
alter table NODO4_CFG.STAZIONI add TARGET_HOST varchar(100);
alter table NODO4_CFG.STAZIONI add TARGET_PORT NUMBER;
alter table NODO4_CFG.STAZIONI add TARGET_PATH varchar(100);
alter table NODO4_CFG.STAZIONI drop column PROTOCOLLO_AVV;
alter table NODO4_CFG.STAZIONI drop column IP_AVV;
alter table NODO4_CFG.STAZIONI drop column PORTA_AVV;
alter table NODO4_CFG.STAZIONI drop column SERVIZIO_AVV;
