package it.pagopa.pagopa.apiconfig.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Table(name = "BINARY_FILE", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BinaryFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "FILE_CONTENT", nullable = false)
    private byte[] fileContent;

    @Column(name = "FILE_HASH", nullable = false)
    private byte[] fileHash;

    @Column(name = "FILE_SIZE", nullable = false)
    private Long fileSize;

    @Column(name = "SIGNATURE_TYPE", length = 30)
    private String signatureType;

    @Lob
    @Column(name = "XML_FILE_CONTENT")
    private String xmlFileContent;

}
