package it.pagopa.pagopa.apiconfig.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "CANALI", schema = "NODO4_CFG")
@Builder(toBuilder = true)
public class Canali implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ID_CANALE", nullable = false, length = 35)
    private String idCanale;

    @Type(type = "yes_no")
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = false;

    @Column(name = "IP", length = 100)
    private String ip;

    @ToString.Exclude
    @Column(name = "NEW_PASSWORD", length = 15)
    private String newPassword;

    @ToString.Exclude
    @Column(name = "PASSWORD", length = 15)
    private String password;

    @Column(name = "PORTA", nullable = false)
    private Long porta;

    @Column(name = "PROTOCOLLO", nullable = false)
    private String protocollo;

    @Column(name = "SERVIZIO", length = 100)
    private String servizio;

    @Column(name = "DESCRIZIONE", length = 70)
    private String descrizione;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_INTERMEDIARIO_PSP", nullable = false)
    @NotNull
    private IntermediariPsp fkIntermediarioPsp;

    @Type(type = "yes_no")
    @Column(name = "PROXY_ENABLED", nullable = false)
    private Boolean proxyEnabled = false;

    @Column(name = "PROXY_HOST", length = 100)
    private String proxyHost;

    @ToString.Exclude
    @Column(name = "PROXY_PASSWORD", length = 15)
    private String proxyPassword;

    @Column(name = "PROXY_PORT")
    private Long proxyPort;

    @Column(name = "PROXY_USERNAME", length = 15)
    private String proxyUsername;

    @Type(type = "yes_no")
    @Column(name = "CANALE_NODO", nullable = false)
    private Boolean canaleNodo = false;

    @Type(type = "yes_no")
    @Column(name = "CANALE_AVV", nullable = false)
    private Boolean canaleAvv = false;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_CANALI_NODO")
    private CanaliNodo fkCanaliNodo;

    @Column(name = "TIMEOUT", nullable = false)
    private Long timeout;

    @Column(name = "NUM_THREAD", nullable = false)
    private Long numThread;

    @Type(type = "yes_no")
    @Column(name = "USE_NEW_FAULT_CODE", nullable = false)
    private Boolean useNewFaultCode = false;

    @Column(name = "TIMEOUT_A", nullable = false)
    private Long timeoutA;

    @Column(name = "TIMEOUT_B", nullable = false)
    private Long timeoutB;

    @Column(name = "TIMEOUT_C", nullable = false)
    private Long timeoutC;

    @Column(name = "SERVIZIO_NMP")
    private String servizioNmp;

}
