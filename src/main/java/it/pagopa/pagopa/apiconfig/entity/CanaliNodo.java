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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "CANALI_NODO", schema = "NODO4_CFG")
@Builder
public class CanaliNodo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "REDIRECT_IP", length = 100)
    private String redirectIp;

    @Column(name = "REDIRECT_PATH", length = 100)
    private String redirectPath;

    @Column(name = "REDIRECT_PORTA")
    private Long redirectPorta;

    @Column(name = "REDIRECT_QUERY_STRING")
    private String redirectQueryString;

    @Column(name = "MODELLO_PAGAMENTO", nullable = false)
    private String modelloPagamento;

    @Type(type = "yes_no")
    @Column(name = "MULTI_PAYMENT", nullable = false)
    private Boolean multiPayment = false;

    @Column(name = "RAGIONE_SOCIALE", length = 35)
    private String ragioneSociale;

    @Type(type = "yes_no")
    @Column(name = "RPT_RT_COMPLIANT", nullable = false)
    private Boolean rptRtCompliant = false;

    @Column(name = "WSAPI", length = 15)
    private String wsapi;

    @Column(name = "REDIRECT_PROTOCOLLO", length = 35)
    private String redirectProtocollo;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SERV_PLUGIN", referencedColumnName = "ID_SERV_PLUGIN")
    private WfespPluginConf idServPlugin;

    @Column(name = "ID_CLUSTER")
    private String idCluster;

    @Column(name = "ID_FESP_INSTANCE", length = 275)
    private String idFespInstance;

    @Type(type = "yes_no")
    @Column(name = "LENTO", nullable = false)
    private Boolean lento = false;

    @Type(type = "yes_no")
    @Column(name = "RT_PUSH", nullable = false)
    private Boolean rtPush = false;

    @Type(type = "yes_no")
    @Column(name = "AGID_CHANNEL", nullable = false)
    private Boolean agidChannel = false;

    @Type(type = "yes_no")
    @Column(name = "ON_US", nullable = false)
    private Boolean onUs = false;

    @Type(type = "yes_no")
    @Column(name = "CARRELLO_CARTE", nullable = false)
    private Boolean carrelloCarte = false;

    @Type(type = "yes_no")
    @Column(name = "RECOVERY", nullable = false)
    private Boolean recovery = false;

    @Type(type = "yes_no")
    @Column(name = "MARCA_BOLLO_DIGITALE", nullable = false)
    private Boolean marcaBolloDigitale = false;

    @Type(type = "yes_no")
    @Column(name = "FLAG_IO")
    private Boolean flagIo;

}
