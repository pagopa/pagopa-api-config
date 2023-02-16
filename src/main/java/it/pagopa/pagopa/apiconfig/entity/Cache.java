package it.pagopa.pagopa.apiconfig.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Table(name = "CACHE", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@java.lang.SuppressWarnings("java:S1700")
public class Cache {

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "CACHE", nullable = false)
    @ToString.Exclude
    private byte[] cache;

    @Column(name = "TIME", nullable = false)
    @ToString.Exclude
    private Timestamp time;

    @Column(name = "VERSION", nullable = false)
    private String version;

}
