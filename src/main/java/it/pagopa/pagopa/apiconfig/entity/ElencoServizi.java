package it.pagopa.pagopa.apiconfig.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "ELENCO_SERVIZI", schema = "NODO4_CFG")
public class ElencoServizi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "PSP_ID", length = 35)
    private String pspId;

    @Column(name = "FLUSSO_ID", length = 35)
    private String flussoId;

    @Column(name = "PSP_RAG_SOC")
    private String pspRagSoc;

    @Type(type = "yes_no")
    @Column(name = "PSP_FLAG_STORNO")
    private Boolean pspFlagStorno;

    @Type(type = "yes_no")
    @Column(name = "PSP_FLAG_BOLLO")
    private Boolean pspFlagBollo;

    @Column(name = "INTM_ID", length = 35)
    private String intmId;

    @Column(name = "CANALE_ID", length = 35)
    private String canaleId;

    @Column(name = "NOME_SERVIZIO", length = 35)
    private String nomeServizio;

    @Column(name = "CANALE_MOD_PAG")
    private Long canaleModPag;

    @Column(name = "TIPO_VERS_COD")
    private String tipoVersCod;

    @Column(name = "CODICE_LINGUA", length = 2)
    private String codiceLingua;

    @Column(name = "INF_COND_EC_MAX", length = 35)
    private String infCondEcMax;

    @Column(name = "INF_DESC_SERV", length = 511)
    private String infDescServ;

    @Column(name = "INF_DISP_SERV", length = 511)
    private String infDispServ;

    @Column(name = "INF_URL_CANALE")
    private String infUrlCanale;

    @Column(name = "IMPORTO_MINIMO")
    private Double importoMinimo;

    @Column(name = "IMPORTO_MASSIMO")
    private Double importoMassimo;

    @Column(name = "COSTO_FISSO")
    private Double costoFisso;

    @Column(name = "TIMESTAMP_INS")
    private Timestamp timestampIns;

    @Column(name = "DATA_VALIDITA")
    private Timestamp dataValidita;

    @Column(name = "LOGO_PSP")
    private byte[] logoPsp;

    @Column(name = "TAGS", length = 135)
    private String tags;

    @Column(name = "LOGO_SERVIZIO")
    private byte[] logoServizio;

    @Type(type = "yes_no")
    @Column(name = "CANALE_APP")
    private Boolean canaleApp;

    @Type(type = "yes_no")
    @Column(name = "ON_US")
    private Boolean onUs;

    @Type(type = "yes_no")
    @Column(name = "CARRELLO_CARTE")
    private Boolean carrelloCarte;

    @Column(name = "CODICE_ABI", length = 5)
    private String codiceAbi;

    @Column(name = "CODICE_MYBANK", length = 35)
    private String codiceMybank;

    @Column(name = "CODICE_CONVENZIONE", length = 35)
    private String codiceConvenzione;

    @Type(type = "yes_no")
    @Column(name = "FLAG_IO")
    private Boolean flagIo;

}
