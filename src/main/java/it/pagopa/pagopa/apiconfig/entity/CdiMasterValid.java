package it.pagopa.pagopa.apiconfig.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Entity;
import javax.persistence.Table;


@Table(name = "CDI_VALIDI_PER_PSP", schema = "NODO4_CFG")
@Entity
@Immutable
@Getter
@Setter
@SuperBuilder
@ToString
@AllArgsConstructor
public class CdiMasterValid extends CdiMaster {

}
