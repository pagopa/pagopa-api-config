package it.pagopa.pagopa.apiconfig.entity;

import it.pagopa.pagopa.apiconfig.util.YesNoConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "STAZIONI", schema = "NODO4_CFG")
@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Stazioni {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")

    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_STAZIONE")
    private String idStazione;

    @Column(name = "ENABLED")
    @Convert(converter = YesNoConverter.class)
    private Boolean enabled;

    @Column(name = "IP")
    private String ip;

    @ToString.Exclude
    @Column(name = "NEW_PASSWORD")
    private String newPassword;

    @ToString.Exclude
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "PORTA")
    private Long porta;

    @Column(name = "PROTOCOLLO", nullable = false)
    private String protocollo;

    @Column(name = "REDIRECT_IP")
    private String redirectIp;

    @Column(name = "REDIRECT_PATH")
    private String redirectPath;

    @Column(name = "REDIRECT_PORTA")
    private Long redirectPorta;

    @Column(name = "REDIRECT_QUERY_STRING")
    private String redirectQueryString;

    @Column(name = "SERVIZIO")
    private String servizio;

    @Column(name = "RT_ENABLED")
    @Convert(converter = YesNoConverter.class)
    private Boolean rtEnabled = true;

    @Column(name = "SERVIZIO_POF")
    private String servizioPof;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_INTERMEDIARIO_PA", nullable = false)
    @ToString.Exclude
    private IntermediariPa intermediarioPa;

    @Column(name = "FK_INTERMEDIARIO_PA", nullable = false, insertable = false, updatable = false)
    private Long fkIntermediarioPa;

    @Column(name = "REDIRECT_PROTOCOLLO")
    private String redirectProtocollo;

    @Column(name = "PROTOCOLLO_4MOD")
    private String protocollo4Mod;

    @Column(name = "IP_4MOD")
    private String ip4Mod;

    @Column(name = "PORTA_4MOD")
    private Long porta4Mod;

    @Column(name = "SERVIZIO_4MOD")
    private String servizio4Mod;

    @Column(name = "PROXY_ENABLED")
    @Convert(converter = YesNoConverter.class)
    private Boolean proxyEnabled;

    @Column(name = "PROXY_HOST")
    private String proxyHost;

    @Column(name = "PROXY_PORT")
    private Long proxyPort;

    @Column(name = "PROXY_USERNAME")
    private String proxyUsername;

    @ToString.Exclude
    @Column(name = "PROXY_PASSWORD")
    private String proxyPassword;

    @Column(name = "NUM_THREAD")
    private Long numThread;

    @Column(name = "TIMEOUT_A")
    private Long timeoutA;

    @Column(name = "TIMEOUT_B")
    private Long timeoutB;

    @Column(name = "TIMEOUT_C")
    private Long timeoutC;

    @Column(name = "FLAG_ONLINE")
    @Convert(converter = YesNoConverter.class)
    private Boolean flagOnline;

    @Column(name = "VERSIONE")
    private Long versione;

    @Column(name = "SERVIZIO_NMP")
    private String servizioNmp;

    @Column(name = "INVIO_RT_ISTANTANEO")
    @Convert(converter = YesNoConverter.class)
    private Boolean invioRtIstantaneo;

}
