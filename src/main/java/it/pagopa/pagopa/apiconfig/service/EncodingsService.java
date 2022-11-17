package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Codifiche;
import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionEncodings;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Encoding;
import it.pagopa.pagopa.apiconfig.repository.CodifichePaRepository;
import it.pagopa.pagopa.apiconfig.repository.CodificheRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Validated
public class EncodingsService {

    @Autowired
    private PaRepository paRepository;

    @Autowired
    private CodificheRepository codificheRepository;

    @Autowired
    private CodifichePaRepository codifichePaRepository;

    @Autowired
    private ModelMapper modelMapper;


    public CreditorInstitutionEncodings getCreditorInstitutionEncodings(@NotNull String creditorInstitutionCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        List<CodifichePa> encodings = codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId());
        return CreditorInstitutionEncodings.builder()
                .encodings(getEncodingList(encodings))
                .build();
    }

    public Encoding createCreditorInstitutionEncoding(@NotNull String creditorInstitutionCode, @NotNull Encoding encoding) {
        // check input
        Pa pa = getPaIfExists(creditorInstitutionCode);
        if (codifichePaRepository.findByCodicePa(encoding.getEncodingCode()).isPresent()) {
            throw new AppException(AppError.ENCODING_CREDITOR_INSTITUTION_CONFLICT, encoding.getEncodingCode(), creditorInstitutionCode);
        }

        // check if encoding is deprecated
        if (encoding.getCodeType().isDeprecated()) {
            throw new AppException(AppError.ENCODING_CREDITOR_INSTITUTION_DEPRECATED, encoding.getCodeType(), encoding.getEncodingCode());
        }

        // check encoding code length
        // QR-CODE must be 11 digits
        // BARCODE-128-AIM must be 12 digits
        if ((encoding.getCodeType().equals(Encoding.CodeTypeEnum.QR_CODE) && encoding.getEncodingCode().length() != 11) ||
                (encoding.getCodeType().equals(Encoding.CodeTypeEnum.BARCODE_128_AIM) && encoding.getEncodingCode().length() != 12)
        ) {
            throw new AppException(AppError.ENCODING_CREDITOR_INSTITUTION_BAD_REQUEST, encoding.getCodeType(), encoding.getEncodingCode());
        }

        Codifiche codifiche = codificheRepository.findByIdCodifica(encoding.getCodeType().getValue())
                .orElseThrow(() -> new AppException(AppError.ENCODING_CREDITOR_INSTITUTION_NOT_FOUND, encoding.getCodeType().getValue()));

        // add ids into object for model mapper
        encoding.setPaObjId(pa.getObjId());
        encoding.setCodificheObjId(codifiche.getObjId());

        // convert and save
        CodifichePa codifichePa = modelMapper.map(encoding, CodifichePa.class);
        CodifichePa result = codifichePaRepository.save(codifichePa);
        return modelMapper.map(result, Encoding.class);
    }

    public void deleteCreditorInstitutionEncoding(@NotNull String creditorInstitutionCode, @NotNull String encodingCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        CodifichePa encoding = codifichePaRepository.findByCodicePaAndFkPa_ObjId(encodingCode, pa.getObjId())
                .orElseThrow(() -> new AppException(AppError.ENCODING_CREDITOR_INSTITUTION_NOT_FOUND, encodingCode, creditorInstitutionCode));
        codifichePaRepository.delete(encoding);
    }

    /**
     * @param creditorInstitutionCode idDominio
     * @return return the PA record from DB if Exists
     * @throws AppException if not found
     */
    private Pa getPaIfExists(@NotNull String creditorInstitutionCode) {
        return paRepository.findByIdDominio(creditorInstitutionCode)
                .orElseThrow(() -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, creditorInstitutionCode));
    }


    /**
     * @param encodings list of {@link CodifichePa}
     * @return maps into a list of {@link Encoding}
     */
    private List<Encoding> getEncodingList(List<CodifichePa> encodings) {
        return encodings.stream()
                .filter(Objects::nonNull)
                .filter(elem -> elem.getFkCodifica() != null)
                .map(elem -> modelMapper.map(elem, Encoding.class))
                .collect(Collectors.toList());
    }

}
