package it.pagopa.pagopa.apiconfig.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "FTP_SERVERS", schema = "NODO4_CFG")
@Builder(toBuilder = true)
public class FtpServers implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "HOST", nullable = false, length = 255)
    private String host;

    @Column(name = "PORT", nullable = false)
    private Integer port;

    @Column(name = "USERNAME", nullable = false, length = 35)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 35)
    private String password;

    @Column(name = "ROOT_PATH", nullable = false, length = 255)
    private String rootPath;

    @Column(name = "SERVICE", nullable = false, length = 255)
    private String service;

    @Column(name = "TYPE", nullable = false, length = 255)
    private String type;

    @Column(name = "IN_PATH", length = 255)
    private String inPath;

    @Column(name = "OUT_PATH", length = 255)
    private String outPath;

    @Column(name = "HISTORY_PATH", length = 255)
    private String historyPath;

    @Type(type = "yes_no")
    @Column(name = "ENABLED")
    private Boolean enabled;

}
