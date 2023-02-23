package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPaymentServiceProviderDetailsToPsp
    implements Converter<PaymentServiceProviderDetails, Psp> {

  @Override
  public Psp convert(MappingContext<PaymentServiceProviderDetails, Psp> context) {
    PaymentServiceProviderDetails source = context.getSource();
    return Psp.builder()
        .idPsp(source.getPspCode())
        .enabled(source.getEnabled())
        .ragioneSociale(source.getBusinessName())
        .abi(source.getAbi())
        .bic(source.getBic())
        .stornoPagamento(source.getTransfer())
        .codiceMybank(source.getMyBankCode())
        .marcaBolloDigitale(source.getStamp())
        .agidPsp(source.getAgidPsp())
        .codiceFiscale(source.getTaxCode())
        .vatNumber(source.getVatNumber())
        .pspNodo(true)
        .pspAvv(false)
        .flagRepoCommissioneCaricoPa(false)
        .build();
  }
}
