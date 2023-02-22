package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPspToPaymentServiceProviderDetails
    implements Converter<Psp, PaymentServiceProviderDetails> {

  @Override
  public PaymentServiceProviderDetails convert(
      MappingContext<Psp, PaymentServiceProviderDetails> context) {
    Psp source = context.getSource();
    return PaymentServiceProviderDetails.builder()
        .pspCode(source.getIdPsp())
        .enabled(source.getEnabled())
        .businessName(CommonUtil.deNull(source.getRagioneSociale()))
        .abi(source.getAbi())
        .bic(source.getBic())
        .transfer(source.getStornoPagamento())
        .myBankCode(source.getCodiceMybank())
        .stamp(source.getMarcaBolloDigitale())
        .agidPsp(source.getAgidPsp())
        .taxCode(source.getCodiceFiscale())
        .vatNumber(source.getVatNumber())
        .build();
  }
}
