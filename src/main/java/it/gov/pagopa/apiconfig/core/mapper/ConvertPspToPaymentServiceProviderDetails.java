package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProviderDetails;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

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
