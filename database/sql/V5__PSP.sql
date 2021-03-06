create table NODO4_CFG.INTERMEDIARI_PSP
(
    OBJ_ID               NUMBER generated by default on null as identity
        constraint PK_INTERMEDIARI_PSP
            primary key,
    ID_INTERMEDIARIO_PSP VARCHAR2(35 char)        not null
        constraint UQ_ID_INTERMEDIARIO_PSP
            unique,
    ENABLED              CHAR(1 char)             not null,
    CODICE_INTERMEDIARIO VARCHAR2(255 char),
    INTERMEDIARIO_AVV    CHAR(1 char) default 'N' not null,
    INTERMEDIARIO_NODO   CHAR(1 char) default 'N' not null,
    FAULT_BEAN_ESTESO    CHAR(1 char) default 'N' not null
);

create table NODO4_CFG.PSP
(
    OBJ_ID                           NUMBER generated by default on null as identity
        constraint PK_PSP
            primary key,
    ID_PSP                           VARCHAR2(35 char)        not null
        constraint UQ_ID_PSP
            unique,
    ENABLED                          CHAR(1 char)             not null,
    ABI                              VARCHAR2(5 char),
    BIC                              VARCHAR2(11 char),
    DESCRIZIONE                      VARCHAR2(70 char),
    RAGIONE_SOCIALE                  VARCHAR2(70 char),
    FK_INT_QUADRATURE                NUMBER(19)
        constraint FK_PSP_INT_QUADRATURE
            references NODO4_CFG.INTERMEDIARI_PSP,
    STORNO_PAGAMENTO                 NUMBER(19)   default '0' not null,
    FLAG_REPO_COMMISSIONE_CARICO_PA  CHAR(1 char),
    EMAIL_REPO_COMMISSIONE_CARICO_PA VARCHAR2(255 char),
    CODICE_MYBANK                    VARCHAR2(35 char),
    MARCA_BOLLO_DIGITALE             NUMBER                   not null,
    AGID_PSP                         CHAR(1 char)             not null,
    PSP_NODO                         CHAR(1 char) default 'N' not null,
    PSP_AVV                          CHAR(1 char) default 'N' not null,
    CODICE_FISCALE                   VARCHAR2(16 char),
    VAT_NUMBER                       VARCHAR2(20 char)
);

INSERT INTO NODO4_CFG.INTERMEDIARI_PSP (OBJ_ID, ID_INTERMEDIARIO_PSP, ENABLED, CODICE_INTERMEDIARIO, INTERMEDIARIO_AVV, INTERMEDIARIO_NODO, FAULT_BEAN_ESTESO) VALUES (6, '17103880000', 'Y', 'Postecom', 'N', 'Y', 'N');

INSERT INTO NODO4_CFG.PSP (OBJ_ID, ID_PSP, ENABLED, ABI, BIC, DESCRIZIONE, RAGIONE_SOCIALE, FK_INT_QUADRATURE, STORNO_PAGAMENTO, FLAG_REPO_COMMISSIONE_CARICO_PA, EMAIL_REPO_COMMISSIONE_CARICO_PA, CODICE_MYBANK, MARCA_BOLLO_DIGITALE, AGID_PSP, PSP_NODO, PSP_AVV, CODICE_FISCALE, VAT_NUMBER) VALUES (1, 'BPPIITRRZZZ', 'Y', '01600', 'BPPIITRRZZZ', 'Poste Italiane', 'Poste Italiane', 6, 0, 'N', null, null, 0, 'N', 'Y', 'N', '17103880000', null);


commit;
