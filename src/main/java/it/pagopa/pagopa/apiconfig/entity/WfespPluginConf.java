package it.pagopa.pagopa.apiconfig.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "WFESP_PLUGIN_CONF", schema = "NODO4_CFG")
@Builder(toBuilder = true)
public class WfespPluginConf implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
  @SequenceGenerator(
      name = "hibernate_sequence",
      sequenceName = "hibernate_sequence",
      allocationSize = 1)
  @Column(name = "OBJ_ID", nullable = false)
  private Long id;

  @Column(name = "ID_SERV_PLUGIN", nullable = false, length = 35)
  private String idServPlugin;

  @Column(name = "PROFILO_PAG_CONST_STRING", length = 150)
  private String profiloPagConstString;

  @Column(name = "PROFILO_PAG_SOAP_RULE", length = 150)
  private String profiloPagSoapRule;

  @Column(name = "PROFILO_PAG_RPT_XPATH", length = 150)
  private String profiloPagRptXpath;

  @Column(name = "ID_BEAN")
  private String idBean;
}
