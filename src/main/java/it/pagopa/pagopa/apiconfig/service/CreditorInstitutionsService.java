package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.*;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.*;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.repository.*;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CreditorInstitutionsService {

  public static final String BAD_RELATION_INFO = "Bad Relation info";
  @Autowired private PaRepository paRepository;

  @Autowired private StazioniRepository stazioniRepository;

  @Autowired private PaStazionePaRepository paStazionePaRepository;

  @Autowired private IbanValidiPerPaRepository ibanValidiPerPaRepository;

  @Autowired private CodifichePaRepository codifichePaRepository;

  @Autowired private CodificheRepository codificheRepository;

  @Autowired private ModelMapper modelMapper;

  public CreditorInstitutions getCreditorInstitutions(
      @NotNull Integer limit, @NotNull Integer pageNumber, @Valid FilterAndOrder filterAndOrder) {
    Pageable pageable = PageRequest.of(pageNumber, limit, CommonUtil.getSort(filterAndOrder));
    var filters =
        CommonUtil.getFilters(
            Pa.builder()
                .idDominio(filterAndOrder.getFilter().getCode())
                .ragioneSociale(filterAndOrder.getFilter().getName())
                .build());
    Page<Pa> page = paRepository.findAll(filters, pageable);
    return CreditorInstitutions.builder()
        .creditorInstitutionList(getCreditorInstitutions(page))
        .pageInfo(CommonUtil.buildPageInfo(page))
        .build();
  }

  public CreditorInstitutionDetails getCreditorInstitution(
      @NotNull String creditorInstitutionCode) {
    Pa pa = getPaIfExists(creditorInstitutionCode);
    return modelMapper.map(pa, CreditorInstitutionDetails.class);
  }

  @Transactional
  public CreditorInstitutionDetails createCreditorInstitution(
      @NotNull CreditorInstitutionDetails creditorInstitutionDetails) {
    if (paRepository
        .findByIdDominio(creditorInstitutionDetails.getCreditorInstitutionCode())
        .isPresent()) {
      throw new AppException(
          AppError.CREDITOR_INSTITUTION_CONFLICT,
          creditorInstitutionDetails.getCreditorInstitutionCode());
    }
    Pa pa = paRepository.save(modelMapper.map(creditorInstitutionDetails, Pa.class));

    addQrEncoding(pa);
    return modelMapper.map(pa, CreditorInstitutionDetails.class);
  }

  public CreditorInstitutionDetails updateCreditorInstitution(
      @Size(max = 50) String creditorInstitutionCode,
      @NotNull CreditorInstitutionDetails creditorInstitutionDetails) {
    Long objId = getPaIfExists(creditorInstitutionCode).getObjId();
    Pa pa = modelMapper.map(creditorInstitutionDetails, Pa.class).toBuilder().objId(objId).build();
    Pa result = paRepository.save(pa);
    return modelMapper.map(result, CreditorInstitutionDetails.class);
  }

  public void deleteCreditorInstitution(@NotNull String creditorInstitutionCode) {
    Pa pa = getPaIfExists(creditorInstitutionCode);
    paRepository.delete(pa);
  }

  public CreditorInstitutionStationList getCreditorInstitutionStations(
      @NotNull String creditorInstitutionCode) {
    Pa pa = getPaIfExists(creditorInstitutionCode);
    List<PaStazionePa> result = paStazionePaRepository.findAllByFkPa(pa.getObjId());
    return CreditorInstitutionStationList.builder().stationsList(getStationsList(result)).build();
  }

  /**
   * Set the aux-digit to null if it is equals to 0 or 3
   *
   * @param creditorInstitutionStationEdit request
   */
  private static void setAuxDigitNull(
      CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
    if (creditorInstitutionStationEdit.getAuxDigit().equals(0L)
        || creditorInstitutionStationEdit.getAuxDigit().equals(3L)) {
      creditorInstitutionStationEdit.setAuxDigit(null);
    }
  }

  @Transactional
  public CreditorInstitutionStationEdit createCreditorInstitutionStation(
      String creditorInstitutionCode,
      CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
    // check aux-digit, application and segregation codes are configured properly
    checkAuxDigit(creditorInstitutionCode, creditorInstitutionStationEdit);
    // check if the relation already exists
    Pa pa = getPaIfExists(creditorInstitutionCode);
    Stazioni stazioni = getStazioniIfExists(creditorInstitutionStationEdit.getStationCode());
    if (paStazionePaRepository
        .findAllByFkPaAndFkStazione_ObjId(pa.getObjId(), stazioni.getObjId())
        .isPresent()) {
      throw new AppException(
          AppError.RELATION_STATION_CONFLICT,
          creditorInstitutionCode,
          creditorInstitutionStationEdit.getStationCode());
    }

    // check uniqueness rules
    checkSegregationCodePresent(creditorInstitutionStationEdit, pa, null, false);
    checkApplicationCodePresent(creditorInstitutionStationEdit, pa, null, false);

    // add info into object for model mapper
    setAuxDigitNull(creditorInstitutionStationEdit);
    creditorInstitutionStationEdit.setFkPa(pa);
    creditorInstitutionStationEdit.setFkStazioni(stazioni);

    // convert and save
    PaStazionePa entity = modelMapper.map(creditorInstitutionStationEdit, PaStazionePa.class);
    paStazionePaRepository.save(entity);
    return creditorInstitutionStationEdit;
  }

  @Transactional
  public void deleteCreditorInstitutionStation(String creditorInstitutionCode, String stationCode) {
    Pa pa = getPaIfExists(creditorInstitutionCode);
    Stazioni stazioni = getStazioniIfExists(stationCode);
    PaStazionePa paStazionePa =
        paStazionePaRepository
            .findAllByFkPaAndFkStazione_ObjId(pa.getObjId(), stazioni.getObjId())
            .orElseThrow(
                () ->
                    new AppException(
                        AppError.RELATION_STATION_NOT_FOUND, creditorInstitutionCode, stationCode));
    paStazionePaRepository.delete(paStazionePa);
  }

  public Ibans getCreditorInstitutionsIbans(@NotNull String creditorInstitutionCode) {
    Pa pa = getPaIfExists(creditorInstitutionCode);
    List<IbanValidiPerPa> iban = ibanValidiPerPaRepository.findAllByFkPa(pa.getObjId());
    return Ibans.builder().ibanList(getIbanList(iban)).build();
  }

  public CreditorInstitutionList getCreditorInstitutionsByIban(@NotNull String iban) {
    List<IbanValidiPerPa> items =
        ibanValidiPerPaRepository.findAllByIbanAccreditoContainsIgnoreCase(iban);

    List<CreditorInstitution> ciList =
        items.isEmpty()
            ? new ArrayList<>()
            : items.stream()
                .map(i -> paRepository.findById(i.getFkPa()))
                .filter(Optional::isPresent)
                .map(pa -> modelMapper.map(pa.get(), CreditorInstitution.class))
                .collect(Collectors.toList());

    return CreditorInstitutionList.builder().creditorInstitutions(ciList).build();
  }

  public CreditorInstitutionList getCreditorInstitutionByPostalEncoding(String encodingCode) {
    return getCreditorInstitutionByEncoding(
        encodingCode, Encoding.CodeTypeEnum.BARCODE_128_AIM.getValue());
  }

  /**
   * @param encodingCode value of the encoding
   * @param codeType encoding type (see: {@link Encoding.CodeTypeEnum})
   * @return the list of EC with the encoding equals to {@code encodingCode} and type equals to
   *     {@code codeType}
   */
  private CreditorInstitutionList getCreditorInstitutionByEncoding(
      String encodingCode, String codeType) {
    List<CodifichePa> codifichePa =
        codifichePaRepository.findAllByCodicePaAndFkCodifica_IdCodifica(encodingCode, codeType);
    return CreditorInstitutionList.builder()
        .creditorInstitutions(getCreditorInstitutions(codifichePa))
        .build();
  }

  /**
   * Check application and segregation code according to aux-digit
   *
   * @param creditorInstitutionCode creditor institution code
   * @param creditorInstitutionStationEdit relation info
   */
  private void checkAuxDigit(
      String creditorInstitutionCode,
      CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
    if (!Arrays.asList(0L, 1L, 2L, 3L).contains(creditorInstitutionStationEdit.getAuxDigit())) {
      String message = "AugDigit code error: accepted values are 0, 1, 2, 3";
      throw new AppException(
          AppError.RELATION_STATION_BAD_REQUEST,
          creditorInstitutionCode,
          creditorInstitutionStationEdit.getStationCode(),
          message);
    }

    if (creditorInstitutionStationEdit.getAuxDigit().equals(0L)) {
      checkAuxDigit0(creditorInstitutionCode, creditorInstitutionStationEdit);
    } else if (creditorInstitutionStationEdit.getAuxDigit().equals(1L)
        || creditorInstitutionStationEdit.getAuxDigit().equals(2L)) {
      if (creditorInstitutionStationEdit.getApplicationCode() != null) {
        String message = "Application code error: length must be blank";
        throw new AppException(
            AppError.RELATION_STATION_BAD_REQUEST,
            creditorInstitutionCode,
            creditorInstitutionStationEdit.getStationCode(),
            message);
      }

      if (creditorInstitutionStationEdit.getSegregationCode() != null) {
        String message = "Segregation code error: length must be blank";
        throw new AppException(
            AppError.RELATION_STATION_BAD_REQUEST,
            creditorInstitutionCode,
            creditorInstitutionStationEdit.getStationCode(),
            message);
      }
    } else if (creditorInstitutionStationEdit.getAuxDigit().equals(3L)) {
      checkAuxDigit3(creditorInstitutionCode, creditorInstitutionStationEdit);
    }
  }

  private void checkAuxDigit0(
      String creditorInstitutionCode,
      CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
    // if aux digit is equal to 0,
    // application code must be 2 ciphers
    // segregation code should be blank or 2 ciphers
    // however they have Long type, so we can check if they have at most 2 cipher
    if (creditorInstitutionStationEdit.getApplicationCode() == null
        || (creditorInstitutionStationEdit.getApplicationCode().toString().length() < 1
            || creditorInstitutionStationEdit.getApplicationCode().toString().length() > 2)) {
      String message = "Application code error: length must be 2 ciphers";
      throw new AppException(
          AppError.RELATION_STATION_BAD_REQUEST,
          creditorInstitutionCode,
          creditorInstitutionStationEdit.getStationCode(),
          message);
    }

    if (creditorInstitutionStationEdit.getSegregationCode() != null
        && (creditorInstitutionStationEdit.getSegregationCode().toString().length() < 1
            || creditorInstitutionStationEdit.getSegregationCode().toString().length() > 2)) {
      String message = "Segregation code error: length must be 2 ciphers or blank";
      throw new AppException(
          AppError.RELATION_STATION_BAD_REQUEST,
          creditorInstitutionCode,
          creditorInstitutionStationEdit.getStationCode(),
          message);
    }
  }

  private void checkAuxDigit3(
      String creditorInstitutionCode,
      CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
    // if aux digit is equal to 3,
    // application code should be 2 ciphers or blank
    // segregation code must be 2 ciphers
    // however they have Long type, so we can check if they have at most 2 cipher
    if (creditorInstitutionStationEdit.getApplicationCode() != null
        && (creditorInstitutionStationEdit.getApplicationCode().toString().length() < 1
            || creditorInstitutionStationEdit.getApplicationCode().toString().length() > 2)) {
      String message = "Application code error: length must be 2 ciphers or blank";
      throw new AppException(
          AppError.RELATION_STATION_BAD_REQUEST,
          creditorInstitutionCode,
          creditorInstitutionStationEdit.getStationCode(),
          message);
    }

    if (creditorInstitutionStationEdit.getSegregationCode() == null
        || (creditorInstitutionStationEdit.getSegregationCode().toString().length() < 1
            || creditorInstitutionStationEdit.getSegregationCode().toString().length() > 2)) {
      String message = "Segregation code error: length must be 2 ciphers";
      throw new AppException(
          AppError.RELATION_STATION_BAD_REQUEST,
          creditorInstitutionCode,
          creditorInstitutionStationEdit.getStationCode(),
          message);
    }
  }

  /**
   * @param stationCode idStazione
   * @return return the Stazioni record from DB if Exists
   * @throws AppException if not found
   */
  private Stazioni getStazioniIfExists(String stationCode) {
    return stazioniRepository
        .findByIdStazione(stationCode)
        .orElseThrow(() -> new AppException(AppError.STATION_NOT_FOUND, stationCode));
  }

  /**
   * @param creditorInstitutionCode idDominio
   * @return return the PA record from DB if Exists
   * @throws AppException if not found
   */
  protected Pa getPaIfExists(String creditorInstitutionCode) throws AppException {
    Optional<Pa> result = paRepository.findByIdDominio(creditorInstitutionCode);
    if (result.isEmpty()) {
      throw new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, creditorInstitutionCode);
    }
    return result.get();
  }

  /**
   * Maps a list of PaStazionePa into a list of CreditorInstitutionStation
   *
   * @param paStazionePaList list of {@link PaStazionePa}
   * @return the list of {@link CreditorInstitutionStation}
   */
  private List<CreditorInstitutionStation> getStationsList(List<PaStazionePa> paStazionePaList) {
    return paStazionePaList.stream()
        .filter(Objects::nonNull)
        .map(elem -> modelMapper.map(elem, CreditorInstitutionStation.class))
        .collect(Collectors.toList());
  }

  /**
   * Maps PA objects stored in the DB in a List of CreditorInstitution
   *
   * @param page page of PA returned from the database
   * @return a list of {@link CreditorInstitutionDetails}.
   */
  private List<CreditorInstitution> getCreditorInstitutions(Page<Pa> page) {
    return page.stream()
        .map(elem -> modelMapper.map(elem, CreditorInstitution.class))
        .collect(Collectors.toList());
  }

  /**
   * @param ibans List of valid ibans
   * @return map elements into a list of {@link Iban}
   */
  private List<Iban> getIbanList(List<IbanValidiPerPa> ibans) {
    return ibans.stream()
        .filter(Objects::nonNull)
        .map(elem -> modelMapper.map(elem, Iban.class))
        .collect(Collectors.toList());
  }

  private void checkApplicationCodePresent(
      CreditorInstitutionStationEdit creditorInstitutionStationEdit,
      Pa pa,
      PaStazionePa paStazionePa,
      boolean edit) {
    String msg = "ApplicationCode already exists";
    List<PaStazionePa> allByPaAndProgressivo =
        paStazionePaRepository.findAllByFkPaAndProgressivo(
            pa.getObjId(), creditorInstitutionStationEdit.getApplicationCode());
    List<PaStazionePa> allByPaAndProgressivoAndStazione =
        paStazionePaRepository.findAllByFkPaAndProgressivoAndFkStazione_IdStazioneIsNot(
            pa.getObjId(),
            creditorInstitutionStationEdit.getApplicationCode(),
            creditorInstitutionStationEdit.getStationCode());
    if (creditorInstitutionStationEdit.getAuxDigit() == 3L) {
      // update
      if (edit
          && paStazionePa != null
          && creditorInstitutionStationEdit.getApplicationCode() != null
          && (allByPaAndProgressivo.size() > 1
              || (allByPaAndProgressivo.size() == 1
                  && allByPaAndProgressivo.stream()
                      .noneMatch(ti -> ti.getObjId().equals(paStazionePa.getObjId()))))) {
        throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, msg);
      }
      // create
      if (!edit
          && creditorInstitutionStationEdit.getApplicationCode() != null
          && !allByPaAndProgressivo.isEmpty()) {
        throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, msg);
      }
    } else if (edit && !allByPaAndProgressivoAndStazione.isEmpty()) {
      throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, msg);
    } else if (!edit && !allByPaAndProgressivo.isEmpty()) {
      throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, msg);
    }
  }

  private void checkSegregationCodePresent(
      CreditorInstitutionStationEdit creditorInstitutionStationEdit,
      Pa pa,
      PaStazionePa paStazionePa,
      boolean edit) {
    String msg = "SegregationCode already exists";
    List<PaStazionePa> allByPaAndSegregazione =
        paStazionePaRepository.findAllByFkPaAndSegregazione(
            pa.getObjId(), creditorInstitutionStationEdit.getSegregationCode());
    List<PaStazionePa> allByPaAndSegregazioneAndStazione =
        paStazionePaRepository.findAllByFkPaAndSegregazioneAndFkStazione_IdStazioneIsNot(
            pa.getObjId(),
            creditorInstitutionStationEdit.getSegregationCode(),
            creditorInstitutionStationEdit.getStationCode());
    if (creditorInstitutionStationEdit.getAuxDigit() == 0L) {
      // update
      if (edit
          && paStazionePa != null
          && creditorInstitutionStationEdit.getSegregationCode() != null
          && (allByPaAndSegregazione.size() > 1
              || (allByPaAndSegregazione.size() == 1
                  && allByPaAndSegregazione.stream()
                      .noneMatch(ti -> ti.getObjId().equals(paStazionePa.getObjId()))))) {
        throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, msg);
      }
      // create
      if (!edit
          && creditorInstitutionStationEdit.getSegregationCode() != null
          && !allByPaAndSegregazione.isEmpty()) {
        throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, msg);
      }
    } else if (edit && !allByPaAndSegregazioneAndStazione.isEmpty()) {
      throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, msg);
    } else if (!edit && !allByPaAndSegregazione.isEmpty()) {
      throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, msg);
    }
  }

  @Transactional
  public CreditorInstitutionStationEdit updateCreditorInstitutionStation(
      String creditorInstitutionCode,
      String stationCode,
      CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
    // check if the relation exists
    Pa pa = getPaIfExists(creditorInstitutionCode);
    Stazioni stazioni = getStazioniIfExists(stationCode);

    creditorInstitutionStationEdit.setFkPa(pa);
    creditorInstitutionStationEdit.setFkStazioni(stazioni);
    creditorInstitutionStationEdit.setStationCode(stationCode);

    PaStazionePa paStazionePa =
        paStazionePaRepository
            .findAllByFkPaAndFkStazione_ObjId(pa.getObjId(), stazioni.getObjId())
            .orElseThrow(
                () ->
                    new AppException(
                        AppError.RELATION_STATION_NOT_FOUND, creditorInstitutionCode, stationCode));

    // check aux-digit, application and segregation codes are configured properly
    checkAuxDigit(creditorInstitutionCode, creditorInstitutionStationEdit);

    // check uniqueness rules
    checkSegregationCodePresent(creditorInstitutionStationEdit, pa, paStazionePa, true);
    checkApplicationCodePresent(creditorInstitutionStationEdit, pa, paStazionePa, true);

    // add info into object for model mapper
    setAuxDigitNull(creditorInstitutionStationEdit);

    // convert and save
    PaStazionePa entity =
        modelMapper.map(creditorInstitutionStationEdit, PaStazionePa.class).toBuilder()
            .objId(paStazionePa.getObjId())
            .build();
    paStazionePaRepository.save(entity);
    return creditorInstitutionStationEdit;
  }

  private void addQrEncoding(Pa pa) {
    Codifiche codifiche =
        codificheRepository
            .findByIdCodifica(Encoding.CodeTypeEnum.QR_CODE.getValue())
            .orElseThrow(
                () ->
                    new AppException(
                        AppError.ENCODING_CREDITOR_INSTITUTION_NOT_FOUND,
                        Encoding.CodeTypeEnum.QR_CODE.getValue()));

    CodifichePa codifichePa =
        CodifichePa.builder()
            .fkPa(pa)
            .fkCodifica(codifiche)
            .codicePa(
                pa.getIdDominio()) // for QR Code Encoding CODICE_PA is the fiscal code of the EC.
            .build();
    codifichePaRepository.save(codifichePa);
  }

  /**
   * @param codifichePa a list of Encoding relations
   * @return the PAs associated to the relations
   */
  private List<CreditorInstitution> getCreditorInstitutions(List<CodifichePa> codifichePa) {
    return codifichePa.stream()
        .map(CodifichePa::getFkPa)
        .map(pa -> modelMapper.map(pa, CreditorInstitution.class))
        .collect(Collectors.toList());
  }
}
