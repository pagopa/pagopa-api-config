package it.pagopa.pagopa.apiconfig.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "BINARY_FILE", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class BinaryFile {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
  @SequenceGenerator(
      name = "hibernate_sequence",
      sequenceName = "hibernate_sequence",
      allocationSize = 1)
  @Column(name = "OBJ_ID", nullable = false)
  private Long id;

  @Column(name = "FILE_CONTENT", nullable = false)
  @ToString.Exclude
  private byte[] fileContent;

  @Column(name = "FILE_HASH", nullable = false)
  @ToString.Exclude
  private byte[] fileHash;

  @Column(name = "FILE_SIZE", nullable = false)
  private Long fileSize;

  @Column(name = "SIGNATURE_TYPE", length = 30)
  private String signatureType;

  @Lob
  @Column(name = "XML_FILE_CONTENT")
  private String xmlFileContent;
}
