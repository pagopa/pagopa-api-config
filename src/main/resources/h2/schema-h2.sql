-- create tables

create table NODO4_CFG.INTERMEDIARI_PA
(
    OBJ_ID               numeric     not null IDENTITY,
    ID_INTERMEDIARIO_PA  varchar(35) not null,
    ENABLED              boolean     not null,
    CODICE_INTERMEDIARIO varchar(255),
    FAULT_BEAN_ESTESO    boolean     not null default 'N',
    constraint PK_INTERMEDIARI_PA
        primary key (OBJ_ID),
    constraint UQ_ID_INTERMEDIARIO_PA
        unique (ID_INTERMEDIARIO_PA)
);

create table NODO4_CFG.QUADRATURE_SCHED
(
    OBJ_ID                numeric not null IDENTITY,
    ID_SOGGETTO           varchar(35),
    MODALITA              varchar(35),
    TIPO_QUAD             varchar(35),
    DAYS_OF_THE_WEEK_CODE varchar(7),
    JOB_START_DATE        timestamp(6),
    ENABLED               char,
    ON_CLICK              char,
    JOB_END_HOUR          timestamp(6),
    JOB_START_HOUR        timestamp(6),
    TIMESTAMP_LAST_ACTION timestamp(6),
    TIMESTAMP_END         timestamp(6),
    TIMESTAMP_BEGIN       timestamp(6),
    QUAD_LAST_DATE        timestamp(6),
    TYPE_INIZIO_GIORNATA  boolean not null default 'N',
    constraint PK_QUADRATURE_SCHED
        primary key (OBJ_ID)
);
create table NODO4_CFG.PA
(
    OBJ_ID                            numeric     not null IDENTITY,
    ID_DOMINIO                        varchar(35) not null,
    ENABLED                           boolean     not null,
    DESCRIZIONE                       varchar(70),
    RAGIONE_SOCIALE                   varchar(70),
    FK_INT_QUADRATURE                 numeric,
    FLAG_REPO_COMMISSIONE_CARICO_PA   boolean     not null,
    EMAIL_REPO_COMMISSIONE_CARICO_PA  varchar(255),
    INDIRIZZO_DOMICILIO_FISCALE       varchar(255),
    CAP_DOMICILIO_FISCALE             numeric(5),
    SIGLA_PROVINCIA_DOMICILIO_FISCALE varchar(2),
    COMUNE_DOMICILIO_FISCALE          varchar(255),
    DENOMINAZIONE_DOMICILIO_FISCALE   varchar(255),
    PAGAMENTO_PRESSO_PSP              boolean     not null,
    RENDICONTAZIONE_FTP               boolean     not null,
    RENDICONTAZIONE_ZIP               boolean     not null,
    REVOCA_PAGAMENTO                  numeric     not null default 0,
    constraint PK_PA
        primary key (OBJ_ID),
    constraint UQ_ID_DOMINIO
        unique (ID_DOMINIO),
    constraint FK_PA_INT_QUADRATURE
        foreign key (FK_INT_QUADRATURE)
            references NODO4_CFG.INTERMEDIARI_PA
);
create table NODO4_CFG.STAZIONI
(
    OBJ_ID                numeric      not null IDENTITY,
    ID_STAZIONE           varchar(35)  not null,
    ENABLED               boolean      not null,
    IP                    varchar(100),
    NEW_PASSWORD          varchar(15),
    PASSWORD              varchar(15),
    PORTA                 numeric      not null,
    PROTOCOLLO            varchar(255) not null,
    REDIRECT_IP           varchar(100),
    REDIRECT_PATH         varchar(100),
    REDIRECT_PORTA        numeric,
    REDIRECT_QUERY_STRING varchar(255),
    SERVIZIO              varchar(100),
    RT_ENABLED            boolean      not null,
    SERVIZIO_POF          varchar(100),
    FK_INTERMEDIARIO_PA   numeric      not null,
    REDIRECT_PROTOCOLLO   varchar(35),
    PROTOCOLLO_4MOD       varchar(255),
    IP_4MOD               varchar(100),
    PORTA_4MOD            numeric,
    SERVIZIO_4MOD         varchar(255),
    PROXY_ENABLED         boolean,
    PROXY_HOST            varchar(100),
    PROXY_PORT            numeric,
    PROXY_USERNAME        varchar(15),
    PROXY_PASSWORD        varchar(15),
    PROTOCOLLO_AVV        varchar(255),
    IP_AVV                varchar(100),
    PORTA_AVV             numeric,
    SERVIZIO_AVV          varchar(255),
    TIMEOUT               numeric(19)  not null default 120,
    NUM_THREAD            numeric      not null,
    TIMEOUT_A             numeric(19)  not null,
    TIMEOUT_B             numeric(19)  not null,
    TIMEOUT_C             numeric(19)  not null,
    FLAG_ONLINE           char         not null default 'Y',
    VERSIONE              numeric      not null default 1.0,
    SERVIZIO_NMP          varchar(255),
    constraint PK_STAZIONI
        primary key (OBJ_ID),
    constraint UQ_ID_STAZIONE
        unique (ID_STAZIONE),
    constraint FK_STAZIONI_INTERMEDIARI_PA
        foreign key (FK_INTERMEDIARIO_PA)
            references NODO4_CFG.INTERMEDIARI_PA
);
create table NODO4_CFG.PA_STAZIONE_PA
(
    OBJ_ID         numeric not null IDENTITY,
    PROGRESSIVO    numeric,
    FK_PA          numeric not null,
    FK_STAZIONE    numeric not null,
    AUX_DIGIT      numeric,
    SEGREGAZIONE   numeric,
    QUARTO_MODELLO boolean not null,
    STAZIONE_NODO  boolean not null default 'N',
    STAZIONE_AVV   boolean not null default 'N',
    BROADCAST      char    not null default 'N',
    constraint PK_PA_STAZIONE_PA
        primary key (OBJ_ID),
    constraint FK_PA_STAZIONE_PA_PA
        foreign key (FK_PA)
            references NODO4_CFG.PA,
    constraint FK_PA_STAZIONE_PA_STAZIONE
        foreign key (FK_STAZIONE)
            references NODO4_CFG.STAZIONI
);

create table NODO4_CFG.CODIFICHE
(
    OBJ_ID      numeric     not null IDENTITY,
    ID_CODIFICA varchar(35) not null,
    DESCRIZIONE varchar(35),
    constraint PK_CODIFICHE
        primary key (OBJ_ID),
    constraint UQ_ID_CODIFICA
        unique (ID_CODIFICA)
);
create table NODO4_CFG.CODIFICHE_PA
(
    OBJ_ID      numeric     not null IDENTITY,
    CODICE_PA   varchar(35) not null,
    FK_CODIFICA numeric     not null,
    FK_PA       numeric     not null,
    constraint PK_CODIFICHE_PA
        primary key (OBJ_ID),
    constraint FK_CODIFICHE
        foreign key (FK_CODIFICA)
            references NODO4_CFG.CODIFICHE,
    constraint FK_CODIFICHE_PA_PA
        foreign key (FK_PA)
            references NODO4_CFG.PA
);
create table NODO4_CFG.BINARY_FILE
(
    OBJ_ID           numeric not null IDENTITY,
    FILE_CONTENT     blob    not null,
    FILE_HASH        blob    not null,
    FILE_SIZE        numeric not null,
    SIGNATURE_TYPE   varchar(30),
    XML_FILE_CONTENT clob,
    constraint PK_BINARY_FILE
        primary key (OBJ_ID)
);
create table NODO4_CFG.INFORMATIVE_CONTO_ACCREDITO_MASTER
(
    OBJ_ID                            numeric     not null IDENTITY,
    DATA_INIZIO_VALIDITA              timestamp(6),
    DATA_PUBBLICAZIONE                timestamp(6),
    ID_INFORMATIVA_CONTO_ACCREDITO_PA varchar(35) not null,
    RAGIONE_SOCIALE                   varchar(70),
    FK_PA                             numeric,
    FK_BINARY_FILE                    numeric,
    VERSIONE                          varchar(35),
    constraint PK_INFORMATIVE_CONTO_ACCREDITO_MASTER
        primary key (OBJ_ID),
    constraint FK_INFORMATIVE_CONTO_ACCREDITO_MASTER_PA
        foreign key (FK_PA)
            references NODO4_CFG.PA,
    constraint FK_BINARY_FILE
        foreign key (FK_BINARY_FILE)
            references NODO4_CFG.BINARY_FILE,
    constraint UQ_INFORMATIVA_CONTO_ACCREDITO
        unique (FK_PA, ID_INFORMATIVA_CONTO_ACCREDITO_PA)
);
create table NODO4_CFG.INFORMATIVE_CONTO_ACCREDITO_DETAIL
(
    OBJ_ID                                numeric not null IDENTITY,
    IBAN_ACCREDITO                        varchar(35),
    FK_INFORMATIVA_CONTO_ACCREDITO_MASTER numeric,
    ID_MERCHANT                           varchar(15),
    CHIAVE_AVVIO                          varchar(255),
    CHIAVE_ESITO                          varchar(255),
    ID_BANCA_SELLER                       varchar(50),
    constraint PK_INFORMATIVE_CONTO_ACCREDITO_DETAIL
        primary key (OBJ_ID),
    constraint FK_INFORMATIVA_CONTO_ACCREDITO_MASTER
        foreign key (FK_INFORMATIVA_CONTO_ACCREDITO_MASTER)
            references NODO4_CFG.INFORMATIVE_CONTO_ACCREDITO_MASTER
);
create view NODO4_CFG.IBAN_VALIDI_PER_PA
as
select MAS.FK_PA,
       DET.IBAN_ACCREDITO,
       MAS.DATA_INIZIO_VALIDITA,
       MAS.DATA_PUBBLICAZIONE,
       MAS.RAGIONE_SOCIALE,
       nullif(DET.ID_MERCHANT, '')     ID_MERCHANT,
       nullif(DET.ID_BANCA_SELLER, '') ID_BANCA_SELLER,
       nullif(DET.CHIAVE_AVVIO, '')    CHIAVE_AVVIO,
       nullif(DET.CHIAVE_ESITO, '')    CHIAVE_ESITO,
       DET.OBJ_ID                      OBJ_ID,
       MAS.OBJ_ID                      MASTER_OBJ
from NODO4_CFG.INFORMATIVE_CONTO_ACCREDITO_MASTER MAS
         join (
    select max(OBJ_ID) OBJ_ID
    from NODO4_CFG.INFORMATIVE_CONTO_ACCREDITO_MASTER
    where DATA_INIZIO_VALIDITA <= current_timestamp
    group by FK_PA
    having max(DATA_INIZIO_VALIDITA) <= current_timestamp
) RIGHT_ID_BY_PK
              on MAS.OBJ_ID = RIGHT_ID_BY_PK.OBJ_ID
         join NODO4_CFG.INFORMATIVE_CONTO_ACCREDITO_DETAIL DET
              on MAS.OBJ_ID = DET.FK_INFORMATIVA_CONTO_ACCREDITO_MASTER;



create table NODO4_CFG.INFORMATIVE_PA_MASTER
(
    OBJ_ID               numeric     not null IDENTITY,
    ID_INFORMATIVA_PA    varchar(35) not null,
    DATA_INIZIO_VALIDITA timestamp(6),
    DATA_PUBBLICAZIONE   timestamp(6),
    FK_PA                numeric     not null,
    FK_BINARY_FILE       numeric,
    VERSIONE             varchar(35),
    PAGAMENTI_PRESSO_PSP numeric,
    constraint PK_INFORMATIVE_PA_MASTER
        primary key (OBJ_ID),
    constraint FK_INFORMATIVE_PA_MASTER_PA
        foreign key (FK_PA)
            references NODO4_CFG.PA,
    constraint FK_INFORMATIVE_PA_MASTER_BINARY_FILE
        foreign key (FK_BINARY_FILE)
            references NODO4_CFG.BINARY_FILE,
    constraint UQ_INFORMATIVA_PA
        unique (FK_PA, ID_INFORMATIVA_PA)
);

create table NODO4_CFG.INTERMEDIARI_PSP
(
    OBJ_ID               numeric     not null identity,
    ID_INTERMEDIARIO_PSP varchar(35) not null,
    ENABLED              char(1)     not null,
    CODICE_INTERMEDIARIO varchar(255),
    INTERMEDIARIO_AVV    char(1)     not null default 'N',
    INTERMEDIARIO_NODO   char(1)     not null default 'N',
    FAULT_BEAN_ESTESO    char(1)     not null default 'N',
    constraint PK_INTERMEDIARI_PSP
        primary key (OBJ_ID),
    constraint UQ_ID_INTERMEDIARIO_PSP
        unique (ID_INTERMEDIARIO_PSP)
);

create table NODO4_CFG.PSP
(
    OBJ_ID                           numeric     not null identity,
    ID_PSP                           varchar(35) not null,
    ENABLED                          char(1)     not null,
    ABI                              varchar(5),
    BIC                              varchar(11),
    DESCRIZIONE                      varchar(70),
    RAGIONE_SOCIALE                  varchar(70),
    FK_INT_QUADRATURE                numeric(19),
    STORNO_PAGAMENTO                 numeric(19) not null default '0',
    FLAG_REPO_COMMISSIONE_CARICO_PA  char(1),
    EMAIL_REPO_COMMISSIONE_CARICO_PA varchar(255),
    CODICE_MYBANK                    varchar(35),
    MARCA_BOLLO_DIGITALE             numeric     not null,
    AGID_PSP                         char(1)     not null,
    PSP_NODO                         char(1)     not null default 'N',
    PSP_AVV                          char(1)     not null default 'N',
    CODICE_FISCALE                   varchar(16),
    VAT_NUMBER                       varchar(20),
    constraint PK_PSP
        primary key (OBJ_ID),
    constraint UQ_ID_PSP
        unique (ID_PSP),
    constraint FK_PSP_INT_QUADRATURE
        foreign key (FK_INT_QUADRATURE)
            references NODO4_CFG.INTERMEDIARI_PSP
);

create table NODO4_CFG.WFESP_PLUGIN_CONF
(
    OBJ_ID                   numeric     not null identity,
    ID_SERV_PLUGIN           varchar(35) not null,
    PROFILO_PAG_CONST_STRING varchar(150),
    PROFILO_PAG_SOAP_RULE    varchar(150),
    PROFILO_PAG_RPT_XPATH    varchar(150),
    ID_BEAN                  varchar(255),
    constraint PK_WFESP_PLUGIN_CONF
        primary key (OBJ_ID),
    constraint UQ_ID_SERV_PLUGIN
        unique (ID_SERV_PLUGIN)
);

create table NODO4_CFG.CANALI_NODO
(
    OBJ_ID                numeric      not null identity,
    REDIRECT_IP           varchar(100),
    REDIRECT_PATH         varchar(100),
    REDIRECT_PORTA        numeric,
    REDIRECT_QUERY_STRING varchar(255),
    MODELLO_PAGAMENTO     varchar(255) not null,
    MULTI_PAYMENT         char(1)      not null,
    RAGIONE_SOCIALE       varchar(35),
    RPT_RT_COMPLIANT      char(1)      not null,
    WSAPI                 varchar(15),
    REDIRECT_PROTOCOLLO   varchar(35),
    ID_SERV_PLUGIN        varchar(35),
    ID_CLUSTER            varchar(255),
    ID_FESP_INSTANCE      varchar(275),
    LENTO                 char(1)      not null,
    RT_PUSH               char(1)      not null,
    AGID_CHANNEL          char(1)      not null,
    ON_US                 char(1)      not null,
    CARRELLO_CARTE        char(1)      not null,
    RECOVERY              char(1)      not null default 'N',
    MARCA_BOLLO_DIGITALE  char         not null,
    FLAG_IO               char                  default 'N',
    constraint PK_CANALI_NODO
        primary key (OBJ_ID),
    constraint FK_CANALI_SERV_PLUGIN
        foreign key (ID_SERV_PLUGIN)
            references NODO4_CFG.WFESP_PLUGIN_CONF (ID_SERV_PLUGIN)
);

create table NODO4_CFG.CANALI (
                                  OBJ_ID numeric not null identity,
                                  ID_CANALE varchar(35) not null,
                                  ENABLED char(1) not null,
                                  IP varchar(100),
                                  NEW_PASSWORD varchar(15),
                                  PASSWORD varchar(15),
                                  PORTA numeric not null,
                                  PROTOCOLLO varchar(255) not null,
                                  SERVIZIO varchar(100),
                                  DESCRIZIONE varchar(70),
                                  FK_INTERMEDIARIO_PSP numeric not null,
                                  PROXY_ENABLED char(1) not null,
                                  PROXY_HOST varchar(100),
                                  PROXY_PASSWORD varchar(15),
                                  PROXY_PORT numeric,
                                  PROXY_USERNAME varchar(15),
                                  CANALE_NODO char(1) not null default 'N',
                                  CANALE_AVV char(1) not null default 'N',
                                  FK_CANALI_NODO numeric,
                                  TIMEOUT numeric not null default 120,
                                  NUM_THREAD numeric not null,
                                  USE_NEW_FAULT_CODE char not null,
                                  TIMEOUT_A numeric(19) not null,
                                  TIMEOUT_B numeric(19) not null,
                                  TIMEOUT_C numeric(19) not null,
                                  SERVIZIO_NMP varchar(255),
                                  constraint PK_CANALI
                                      primary key (OBJ_ID),
                                  constraint UQ_ID_CANALE
                                      unique (ID_CANALE),
                                  constraint FK_CANALI_INTERMEDIARI_PSP
                                      foreign key (FK_INTERMEDIARIO_PSP)
                                          references NODO4_CFG.INTERMEDIARI_PSP,
                                  constraint FK_CANALI_NODO
                                      foreign key (FK_CANALI_NODO)
                                          references NODO4_CFG.CANALI_NODO
);

create table NODO4_CFG.ELENCO_SERVIZI (
                                          OBJ_ID numeric not null,
                                          PSP_ID varchar(35),
                                          FLUSSO_ID varchar(35),
                                          PSP_RAG_SOC varchar(255),
                                          PSP_FLAG_STORNO char,
                                          PSP_FLAG_BOLLO char,
                                          INTM_ID varchar(35),
                                          CANALE_ID varchar(35),
                                          NOME_SERVIZIO varchar(35),
                                          CANALE_MOD_PAG numeric,
                                          TIPO_VERS_COD varchar(255),
                                          CODICE_LINGUA char(2),
                                          INF_COND_EC_MAX varchar(35),
                                          INF_DESC_SERV varchar(511),
                                          INF_DISP_SERV varchar(511),
                                          INF_URL_CANALE varchar(255),
                                          IMPORTO_MINIMO float,
                                          IMPORTO_MASSIMO float,
                                          COSTO_FISSO float,
                                          TIMESTAMP_INS timestamp(6),
                                          DATA_VALIDITA timestamp(6),
                                          LOGO_PSP blob,
                                          TAGS varchar(135),
                                          LOGO_SERVIZIO blob,
                                          CANALE_APP char,
                                          ON_US char,
                                          CARRELLO_CARTE char,
                                          CODICE_ABI varchar(5),
                                          CODICE_MYBANK varchar(35),
                                          CODICE_CONVENZIONE varchar(35),
                                          FLAG_IO char,
                                          constraint PK_ELENCO_SERVIZI
                                              primary key (OBJ_ID)
);

create table NODO4_CFG.TIPI_VERSAMENTO (
                                           OBJ_ID numeric not null,
                                           DESCRIZIONE varchar(35),
                                           TIPO_VERSAMENTO varchar(15) not null,
                                           constraint PK_TIPI_VERSAMENTO
                                               primary key (OBJ_ID),
                                           constraint UQ_TIPO_VERSAMENTO
                                               unique (TIPO_VERSAMENTO)
);

create table NODO4_CFG.CANALE_TIPO_VERSAMENTO (
                                                  OBJ_ID numeric not null,
                                                  FK_CANALE numeric not null,
                                                  FK_TIPO_VERSAMENTO numeric not null,
                                                  constraint PK_CANALE_TIPO_VERSAMENTOE
                                                      primary key (OBJ_ID),
                                                  constraint FK_CANALE_TIPO_VERSAMENTO_CANALI
                                                      foreign key (FK_CANALE)
                                                          references NODO4_CFG.CANALI,
                                                  constraint FK_CANALE_TIPO_VERSAMENTO_TIPO_VERSAMENTO
                                                      foreign key (FK_TIPO_VERSAMENTO)
                                                          references NODO4_CFG.TIPI_VERSAMENTO
);

create table NODO4_CFG.PSP_CANALE_TIPO_VERSAMENTO (
                                                      OBJ_ID numeric not null,
                                                      FK_CANALE_TIPO_VERSAMENTO numeric not null,
                                                      FK_PSP numeric not null,
                                                      constraint PK_PSP_CANALE_TIPO_VERSAMENTO
                                                          primary key (OBJ_ID),
                                                      constraint FK_CANALE_TIPO_VERSAMENTO
                                                          foreign key (FK_CANALE_TIPO_VERSAMENTO)
                                                              references NODO4_CFG.CANALE_TIPO_VERSAMENTO,
                                                      constraint FK_PSP_CANALE_TIPO_VERSAMENTO_PSP
                                                          foreign key (FK_PSP)
                                                              references NODO4_CFG.PSP
);
