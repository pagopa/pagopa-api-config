-- Insert data

INSERT INTO NODO4_CFG.INTERMEDIARI_PA (OBJ_ID, ID_INTERMEDIARIO_PA, ENABLED, CODICE_INTERMEDIARIO, FAULT_BEAN_ESTESO)
VALUES (2, '80007580279', 'Y', 'Regione Veneto', 'N');

insert into NODO4_CFG.QUADRATURE_SCHED (OBJ_ID,
                                        ID_SOGGETTO,
                                        MODALITA,
                                        TIPO_QUAD,
                                        DAYS_OF_THE_WEEK_CODE,
                                        JOB_START_DATE,
                                        ENABLED,
                                        ON_CLICK,
                                        JOB_END_HOUR,
                                        JOB_START_HOUR,
                                        TIMESTAMP_LAST_ACTION,
                                        TIMESTAMP_END,
                                        TIMESTAMP_BEGIN,
                                        QUAD_LAST_DATE,
                                        TYPE_INIZIO_GIORNATA)
values (1,
        'INTERMEDIARIOPAP',
        'GIORNALIERA',
        'INT_PA',
        'FTTTTTF',
        parsedatetime('2013-10-17 00:00:00', 'YYYY-MM-DD HH:mm:SS'),
        'N',
        'N',
        parsedatetime('2013-10-24 08:00:35', 'YYYY-MM-DD HH:mm:SS'),
        parsedatetime('2013-10-24 07:00:35', 'YYYY-MM-DD HH:mm:SS'),
        parsedatetime('2013-12-11 07:00:56', 'YYYY-MM-DD HH:mm:SS'),
        parsedatetime('2013-12-11 07:00:56', 'YYYY-MM-DD HH:mm:SS'),
        parsedatetime('2013-12-11 07:00:56', 'YYYY-MM-DD HH:mm:SS'),
        parsedatetime('2013-12-11 00:00:00', 'YYYY-MM-DD HH:mm:SS'),
        'Y');

INSERT INTO NODO4_CFG.PA (OBJ_ID, ID_DOMINIO, ENABLED, DESCRIZIONE, RAGIONE_SOCIALE, FK_INT_QUADRATURE,
                          FLAG_REPO_COMMISSIONE_CARICO_PA, EMAIL_REPO_COMMISSIONE_CARICO_PA,
                          INDIRIZZO_DOMICILIO_FISCALE, CAP_DOMICILIO_FISCALE, SIGLA_PROVINCIA_DOMICILIO_FISCALE,
                          COMUNE_DOMICILIO_FISCALE, DENOMINAZIONE_DOMICILIO_FISCALE, PAGAMENTO_PRESSO_PSP,
                          RENDICONTAZIONE_FTP, RENDICONTAZIONE_ZIP, REVOCA_PAGAMENTO)
VALUES (190, '00168480242', 'Y', 'Comune di Bassano del Grappa', 'Comune di Bassano del Grappa', null, 'N', null, null,
        null, null, null, null, 'Y', 'N', 'N', 0);

INSERT INTO NODO4_CFG.PA (OBJ_ID, ID_DOMINIO, ENABLED, DESCRIZIONE, RAGIONE_SOCIALE, FK_INT_QUADRATURE,
                          FLAG_REPO_COMMISSIONE_CARICO_PA, EMAIL_REPO_COMMISSIONE_CARICO_PA,
                          INDIRIZZO_DOMICILIO_FISCALE, CAP_DOMICILIO_FISCALE, SIGLA_PROVINCIA_DOMICILIO_FISCALE,
                          COMUNE_DOMICILIO_FISCALE, DENOMINAZIONE_DOMICILIO_FISCALE, PAGAMENTO_PRESSO_PSP,
                          RENDICONTAZIONE_FTP, RENDICONTAZIONE_ZIP, REVOCA_PAGAMENTO)
VALUES (191, '1110001', 'Y', 'Comune di Roma', 'Comune di Roma', null, 'N', null, null,
        null, null, null, null, 'Y', 'N', 'N', 0);

INSERT INTO NODO4_CFG.STAZIONI (OBJ_ID, ID_STAZIONE, ENABLED, IP, NEW_PASSWORD, PASSWORD, PORTA, PROTOCOLLO,
                                REDIRECT_IP, REDIRECT_PATH, REDIRECT_PORTA, REDIRECT_QUERY_STRING, SERVIZIO, RT_ENABLED,
                                SERVIZIO_POF, FK_INTERMEDIARIO_PA, REDIRECT_PROTOCOLLO, PROTOCOLLO_4MOD, IP_4MOD,
                                PORTA_4MOD, SERVIZIO_4MOD, PROXY_ENABLED, PROXY_HOST, PROXY_PORT, PROXY_USERNAME,
                                PROXY_PASSWORD, PROTOCOLLO_AVV, IP_AVV, PORTA_AVV, SERVIZIO_AVV, TIMEOUT, NUM_THREAD,
                                TIMEOUT_A, TIMEOUT_B, TIMEOUT_C, FLAG_ONLINE, VERSIONE, SERVIZIO_NMP)
VALUES (2, '80007580279_01', 'Y', 'NodoDeiPagamentiDellaPATest.sia.eu', null, 'password', 80, 'HTTP',
        'paygov.collaudo.regione.veneto.it', 'nodo-regionale-fesp/paaInviaRispostaPagamento.html', 443, null,
        'openspcoop/PD/RT6TPDREGVENETO', 'Y', 'openspcoop/PD/CCP6TPDREGVENETO', 2, 'HTTPS', 'HTTP', null, null, null,
        'Y', '10.101.1.95', 8080, null, null, 'HTTP', null, null, null, 120, 2, 15, 30, 120, 'Y', 1, null);

INSERT INTO NODO4_CFG.PA_STAZIONE_PA (OBJ_ID, PROGRESSIVO, FK_PA, FK_STAZIONE, AUX_DIGIT, SEGREGAZIONE, QUARTO_MODELLO,
                                      STAZIONE_NODO, STAZIONE_AVV, BROADCAST)
VALUES (430, 1, 190, 2, null, 1, 'N', 'Y', 'N', 'N');



INSERT INTO NODO4_CFG.CODIFICHE (OBJ_ID, ID_CODIFICA, DESCRIZIONE)
VALUES (1, 'BARCODE-GS1-128', 'Codifica barcode GS1-128 tot 13');
INSERT INTO NODO4_CFG.CODIFICHE (OBJ_ID, ID_CODIFICA, DESCRIZIONE)
VALUES (2, 'QR-CODE', 'QR-CODE CF  11 caratteri');
INSERT INTO NODO4_CFG.CODIFICHE (OBJ_ID, ID_CODIFICA, DESCRIZIONE)
VALUES (3, 'BARCODE-128-AIM', 'BARCODE-128-AIM zeri + ccp tot 12');


INSERT INTO NODO4_CFG.CODIFICHE_PA (OBJ_ID, CODICE_PA, FK_CODIFICA, FK_PA)
VALUES (219, '00168480242', 2, 190);
INSERT INTO NODO4_CFG.CODIFICHE_PA (OBJ_ID, CODICE_PA, FK_CODIFICA, FK_PA)
VALUES (220, '8088888169189', 1, 190);

INSERT INTO NODO4_CFG.BINARY_FILE (OBJ_ID, FILE_CONTENT, FILE_HASH, FILE_SIZE, SIGNATURE_TYPE, XML_FILE_CONTENT)
VALUES (201,
        '3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D225554462D38223F3E0D0A3C696E666F726D6174697661436F6E746F41636372656469746F20786D6C6E733A7873693D22687474703A2F2F7777772E77332E6F72672F323030312F584D4C536368656D612D696E7374616E636522207873693A6E6F4E616D657370616365536368656D614C6F636174696F6E3D22496E666F726D6174697661436F6E746F41636372656469746F5F315F325F302E787364223E0D0A093C6964656E7469666963617469766F466C7573736F3E435F413730332D4942414E2D32303137303330383030303030303C2F6964656E7469666963617469766F466C7573736F3E0D0A093C6964656E7469666963617469766F446F6D696E696F3E30303136383438303234323C2F6964656E7469666963617469766F446F6D696E696F3E0D0A093C726167696F6E65536F6369616C653E436F6D756E652064692042617373616E6F2064656C204772617070613C2F726167696F6E65536F6369616C653E0D0A093C64617461507562626C6963617A696F6E653E323031372D30332D30385430303A30303A30303C2F64617461507562626C6963617A696F6E653E0D0A093C64617461496E697A696F56616C69646974613E323031372D30332D30395430303A30303A30303C2F64617461496E697A696F56616C69646974613E0D0A093C636F6E7469446941636372656469746F3E0D0A09093C212D2D20636F6F7264696E6174652062616E63617269652064656C20636F6E746F20636F7272656E7465207072696E636970616C65206469207465736F7265726961202D2D3E0D0A09093C6962616E41636372656469746F3E495435304D303230303836303136353030303030333439373438313C2F6962616E41636372656469746F3E0D0A093C2F636F6E7469446941636372656469746F3E0D0A3C2F696E666F726D6174697661436F6E746F41636372656469746F3E0D0A',
        '4B57E35E75CEDADAEF5CF4CCA0D6CF8BE9965725', 724, null, null);
INSERT INTO NODO4_CFG.INFORMATIVE_CONTO_ACCREDITO_MASTER (OBJ_ID, DATA_INIZIO_VALIDITA, DATA_PUBBLICAZIONE,
                                                          ID_INFORMATIVA_CONTO_ACCREDITO_PA, RAGIONE_SOCIALE, FK_PA,
                                                          FK_BINARY_FILE, VERSIONE)
VALUES (228, parsedatetime('2017-03-09 00:00:00', 'YYYY-MM-DD HH:mm:SS'),
        parsedatetime('2017-03-08 00:00:00', 'YYYY-MM-DD HH:mm:SS'), 'C_A703-IBAN-20170308000000',
        'Comune di Bassano del Grappa', 190, 201, null);
INSERT INTO NODO4_CFG.INFORMATIVE_CONTO_ACCREDITO_DETAIL (OBJ_ID, IBAN_ACCREDITO, FK_INFORMATIVA_CONTO_ACCREDITO_MASTER,
                                                          ID_MERCHANT, CHIAVE_AVVIO, CHIAVE_ESITO, ID_BANCA_SELLER)
VALUES (521, 'IT50M0200860165000003497481', 228, null, null, null, null);



INSERT INTO NODO4_CFG.INFORMATIVE_PA_MASTER (OBJ_ID, ID_INFORMATIVA_PA, DATA_INIZIO_VALIDITA, DATA_PUBBLICAZIONE, FK_PA,
                                             FK_BINARY_FILE, VERSIONE, PAGAMENTI_PRESSO_PSP)
VALUES (1, '1', parsedatetime('2014-08-07 12:00:00', 'YYYY-MM-DD HH:mm:SS'),
        parsedatetime('2014-08-06 12:00:00', 'YYYY-MM-DD HH:mm:SS'), 190, 201, null, 0);


INSERT INTO NODO4_CFG.INTERMEDIARI_PSP (OBJ_ID, ID_INTERMEDIARIO_PSP, ENABLED, CODICE_INTERMEDIARIO, INTERMEDIARIO_AVV,
                                        INTERMEDIARIO_NODO, FAULT_BEAN_ESTESO)
VALUES (6, '17103880000', 'Y', 'Postecom', 'N', 'Y', 'N');

INSERT INTO NODO4_CFG.PSP (OBJ_ID, ID_PSP, ENABLED, ABI, BIC, DESCRIZIONE, RAGIONE_SOCIALE, FK_INT_QUADRATURE,
                           STORNO_PAGAMENTO, FLAG_REPO_COMMISSIONE_CARICO_PA, EMAIL_REPO_COMMISSIONE_CARICO_PA,
                           CODICE_MYBANK, MARCA_BOLLO_DIGITALE, AGID_PSP, PSP_NODO, PSP_AVV, CODICE_FISCALE, VAT_NUMBER)
VALUES (1, 'BPPIITRRZZZ', 'Y', '01600', 'BPPIITRRZZZ', 'Poste Italiane', 'Poste Italiane', 6, 0, 'N', null, null, 0,
        'N', 'Y', 'N', '17103880000', null);



insert into NODO4_CFG.WFESP_PLUGIN_CONF (OBJ_ID, ID_SERV_PLUGIN, PROFILO_PAG_CONST_STRING, PROFILO_PAG_SOAP_RULE,
                                         PROFILO_PAG_RPT_XPATH, ID_BEAN)
values (1, 'idPsp1', null, 'profilo=$identificativoIntermediarioPA$~$identificativoStazioneIntermediarioPA$', null,
        'defaultForwardProcessor');


insert into NODO4_CFG.CANALI_NODO (OBJ_ID, REDIRECT_IP, REDIRECT_PATH, REDIRECT_PORTA, REDIRECT_QUERY_STRING,
                                   MODELLO_PAGAMENTO, MULTI_PAYMENT, RAGIONE_SOCIALE, RPT_RT_COMPLIANT, WSAPI,
                                   REDIRECT_PROTOCOLLO, ID_SERV_PLUGIN, ID_CLUSTER, ID_FESP_INSTANCE, LENTO, RT_PUSH,
                                   AGID_CHANNEL, ON_US, CARRELLO_CARTE, RECOVERY, MARCA_BOLLO_DIGITALE, FLAG_IO)
values (1, null, null, null, null, 'ATTIVATO_PRESSO_PSP', 'N', null, 'N', null, 'HTTP', null, 'CL_1', null, 'N', 'Y',
        'N', 'N', 'N', 'Y', 'N', 'N');


insert into NODO4_CFG.CANALI (OBJ_ID, ID_CANALE, ENABLED, IP, NEW_PASSWORD, PASSWORD, PORTA, PROTOCOLLO, SERVIZIO,
                              DESCRIZIONE, FK_INTERMEDIARIO_PSP, PROXY_ENABLED, PROXY_HOST, PROXY_PASSWORD, PROXY_PORT,
                              PROXY_USERNAME, CANALE_NODO, CANALE_AVV, FK_CANALI_NODO, TIMEOUT, NUM_THREAD,
                              USE_NEW_FAULT_CODE, TIMEOUT_A, TIMEOUT_B, TIMEOUT_C, SERVIZIO_NMP)
values (1, '00001060966_01', 'Y', '1.1.1.1', null, 'FakePay', 443, 'HTTPS', 'basepath/services/fake', null, 6, 'Y',
        '2.2.2.2', null, 8080, null, 'Y', 'N', 1, 120, 2, 'Y', 15, 30, 120, null);

insert into NODO4_CFG.ELENCO_SERVIZI (OBJ_ID, PSP_ID, FLUSSO_ID, PSP_RAG_SOC, PSP_FLAG_STORNO, PSP_FLAG_BOLLO, INTM_ID,
                                      CANALE_ID, NOME_SERVIZIO, CANALE_MOD_PAG, TIPO_VERS_COD, CODICE_LINGUA,
                                      INF_COND_EC_MAX, INF_DESC_SERV, INF_DISP_SERV, INF_URL_CANALE, IMPORTO_MINIMO,
                                      IMPORTO_MASSIMO, COSTO_FISSO, TIMESTAMP_INS, DATA_VALIDITA, LOGO_PSP, TAGS,
                                      LOGO_SERVIZIO, CANALE_APP, ON_US, CARRELLO_CARTE, CODICE_ABI, CODICE_MYBANK,
                                      CODICE_CONVENZIONE, FLAG_IO)
values (1, 'BPPIITRRZZZ', 'WISP_1_1_20210101', 'Cassa di Risparmio di Parma e Piacenza S.p.A.', 'N', 'N', '02113530345',
        '00001060966_01', 'non comunicato', 0, 'CP', 'IT', null,
        'Il servizio consente ai clienti di effettuare pagamenti elettronici online verso la Pubblica Amministrazione e i gestori di serviz',
        '24/7', null, 0, 99999999.99, 1.99, to_timestamp('2021-12-10 00:05:09.000000', 'YYYY-MM-DD HH24:MI:SS.FF6'),
        to_timestamp('2017-07-12 00:02:00.000000', 'YYYY-MM-DD HH24:MI:SS.FF6'), '16', 'Diners;V-Pay', '16',
        'N', 'N', 'N', 'TBD', null, null, 'N');

insert into NODO4_CFG.ELENCO_SERVIZI (OBJ_ID, PSP_ID, FLUSSO_ID, PSP_RAG_SOC, PSP_FLAG_STORNO, PSP_FLAG_BOLLO, INTM_ID,
                                      CANALE_ID, NOME_SERVIZIO, CANALE_MOD_PAG, TIPO_VERS_COD, CODICE_LINGUA,
                                      INF_COND_EC_MAX, INF_DESC_SERV, INF_DISP_SERV, INF_URL_CANALE, IMPORTO_MINIMO,
                                      IMPORTO_MASSIMO, COSTO_FISSO, TIMESTAMP_INS, DATA_VALIDITA, LOGO_PSP, TAGS,
                                      LOGO_SERVIZIO, CANALE_APP, ON_US, CARRELLO_CARTE, CODICE_ABI, CODICE_MYBANK,
                                      CODICE_CONVENZIONE, FLAG_IO)
values (2, 'BPPIITRRZZZ', 'WISP_1_1_20210101', 'Cassa di Risparmio di Parma e Piacenza S.p.A.', 'N', 'N', '02113530345',
        '00001060966_01', 'non comunicato', 0, 'CP', 'EN', null,
        'Il servizio consente ai clienti di effettuare pagamenti elettronici online verso la Pubblica Amministrazione e i gestori di serviz',
        '24/7', null, 0, 99999999.99, 1.99, to_timestamp('2021-12-10 00:05:09.000000', 'YYYY-MM-DD HH24:MI:SS.FF6'),
        to_timestamp('2017-07-12 00:02:00.000000', 'YYYY-MM-DD HH24:MI:SS.FF6'), '16', 'Diners;V-Pay', '16',
        'Y', 'Y', 'Y', 'TBD', '1', null, 'Y');

INSERT INTO NODO4_CFG.TIPI_VERSAMENTO (OBJ_ID, DESCRIZIONE, TIPO_VERSAMENTO) VALUES (11636, 'PayPal', 'PPAL');
INSERT INTO NODO4_CFG.TIPI_VERSAMENTO (OBJ_ID, DESCRIZIONE, TIPO_VERSAMENTO) VALUES (1, 'Bollettino postale', 'BP');
INSERT INTO NODO4_CFG.TIPI_VERSAMENTO (OBJ_ID, DESCRIZIONE, TIPO_VERSAMENTO) VALUES (2, 'Bonifico bancario tesoreria', 'BBT');
INSERT INTO NODO4_CFG.TIPI_VERSAMENTO (OBJ_ID, DESCRIZIONE, TIPO_VERSAMENTO) VALUES (3, 'Addebito diretto', 'AD');
INSERT INTO NODO4_CFG.TIPI_VERSAMENTO (OBJ_ID, DESCRIZIONE, TIPO_VERSAMENTO) VALUES (4, 'Carta di pagamento', 'CP');
INSERT INTO NODO4_CFG.TIPI_VERSAMENTO (OBJ_ID, DESCRIZIONE, TIPO_VERSAMENTO) VALUES (5, 'Pagamento attivato presso PSP', 'PO');
INSERT INTO NODO4_CFG.TIPI_VERSAMENTO (OBJ_ID, DESCRIZIONE, TIPO_VERSAMENTO) VALUES (6, 'Online Banking Electronic Payment', 'OBEP');
INSERT INTO NODO4_CFG.TIPI_VERSAMENTO (OBJ_ID, DESCRIZIONE, TIPO_VERSAMENTO) VALUES (7, 'Jiffy', 'JIF');
INSERT INTO NODO4_CFG.TIPI_VERSAMENTO (OBJ_ID, DESCRIZIONE, TIPO_VERSAMENTO) VALUES (8, 'MyBank', 'MYBK');

insert into NODO4_CFG.CANALE_TIPO_VERSAMENTO (OBJ_ID, FK_CANALE, FK_TIPO_VERSAMENTO) values (1,1,1);
insert into NODO4_CFG.CANALE_TIPO_VERSAMENTO (OBJ_ID, FK_CANALE, FK_TIPO_VERSAMENTO) values (2,1,2);

insert into NODO4_CFG.PSP_CANALE_TIPO_VERSAMENTO (OBJ_ID, FK_CANALE_TIPO_VERSAMENTO, FK_PSP)
values (1,1,1);
