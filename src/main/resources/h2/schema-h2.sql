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
