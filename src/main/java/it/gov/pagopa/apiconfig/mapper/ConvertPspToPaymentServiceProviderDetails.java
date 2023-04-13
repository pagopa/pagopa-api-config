package it.gov.pagopa.apiconfig.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.util.CommonUtil;

public class ConvertPspToPaymentServiceProviderDetails
    implements Converter<Psp, PaymentServiceProviderDetails> {

  @Override
  public PaymentServiceProviderDetails convert(
      MappingContext<Psp, PaymentServiceProviderDetails> context) {
    Psp source = context.getSource();
    var output =
        PaymentServiceProviderDetails.builder()
            .abi(source.getAbi())
            .bic(source.getBic())
            .transfer(source.getStornoPagamento())
            .myBankCode(source.getCodiceMybank())
            .stamp(source.getMarcaBolloDigitale())
            .agidPsp(source.getAgidPsp())
            .taxCode(source.getCodiceFiscale())
            .vatNumber(source.getVatNumber())
            .build();

    output.setPspCode(source.getIdPsp());
    output.setEnabled(source.getEnabled());
    output.setBusinessName(CommonUtil.deNull(source.getRagioneSociale()));
    return output;
  }
}
