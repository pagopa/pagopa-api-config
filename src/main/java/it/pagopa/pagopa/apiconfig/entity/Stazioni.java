package it.pagopa.pagopa.apiconfig.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "STAZIONI", schema = "NODO4_CFG")
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Stazioni {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_STAZIONE")
    private String idStazione;

    @Column(name = "ENABLED", columnDefinition = "CHAR")
    private String enabled;

    @Column(name = "IP")
    private String ip;

    @Column(name = "NEW_PASSWORD")
    private String newPassword;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "PORTA")
    private Long porta;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
    @Column(name = "PROTOCOLLO")
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

    @Column(name = "RT_ENABLED", columnDefinition = "CHAR")
    private String rtEnabled;

    @Column(name = "SERVIZIO_POF")
    private String servizioPof;

    @JoinColumn(name = "FK_INTERMEDIARIO_PA")
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

    @Column(name = "PROXY_ENABLED", columnDefinition = "CHAR")
    private String proxyEnabled;

    @Column(name = "PROXY_HOST")
    private String proxyHost;

    @Column(name = "PROXY_PORT")
    private Long proxyPort;

    @Column(name = "PROXY_USERNAME")
    private String proxyUsername;

    @Column(name = "PROXY_PASSWORD")
    private String proxyPassword;

    @Column(name = "PROTOCOLLO_AVV")
    private String protocolloAvv;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
    @Column(name = "IP_AVV")
    private String ipAvv;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
    @Column(name = "PORTA_AVV")
    private Long portaAvv;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
    @Column(name = "SERVIZIO_AVV")
    private String servizioAvv;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
    @Column(name = "TIMEOUT")
    private Long timeout;

    @Column(name = "NUM_THREAD")
    private Long numThread;

    @Column(name = "TIMEOUT_A")
    private Long timeoutA;

    @Column(name = "TIMEOUT_B")
    private Long timeoutB;

    @Column(name = "TIMEOUT_C")
    private Long timeoutC;

    @Column(name = "FLAG_ONLINE", columnDefinition = "CHAR")
    private String flagOnline;

    @Column(name = "VERSIONE")
    private Long versione;

    @Column(name = "SERVIZIO_NMP")
    private String servizioNmp;

}
