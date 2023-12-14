package it.gov.pagopa.apiconfig.core.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.apiconfig.core.model.massiveloading.IbanMassLoadCsv;
import it.gov.pagopa.apiconfig.core.model.massiveloading.IbansMassLoadCsv;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.validator.routines.IBANValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.CheckItem;
import it.gov.pagopa.apiconfig.core.model.CheckItem.Validity;
import it.gov.pagopa.apiconfig.core.model.MassiveCheck;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding.CodeTypeEnum;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanEnhanced;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanLabel;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbansEnhanced;
import it.gov.pagopa.apiconfig.core.model.massiveloading.IbansMassLoad;
import it.gov.pagopa.apiconfig.core.model.massiveloading.IbansMaster;
import it.gov.pagopa.apiconfig.core.scheduler.storage.AzureStorageInteraction;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import it.gov.pagopa.apiconfig.starter.entity.Iban;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttribute;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttributeMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster.IbanStatus;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.repository.CodifichePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;

@Service
@Validated
@Transactional
public class IbanService {

	public static final String FILE_BAD_REQUEST = "Bad request for the massive loading of IBANs";

	@Value("${iban.abi.poste}")
	private String postalIbanAbi;

	@Value("${iban.labels.cup}")
	private String cupLabel;

	@Value("${iban.labels.aca}")
	private String acaLabel;

	@Value("${zip.entries}")
	private int thresholdEntries; // Maximum number of entries allowed in the zip file

	@Value("${zip.size}")
	private int thresholdSize; // Max size of zip file content

	@Autowired private PaRepository paRepository;

	@Autowired private IbanRepository ibanRepository;

	@Autowired private IbanMasterRepository ibanMasterRepository;

	@Autowired private IbanAttributeRepository ibanAttributeRepository;

	@Autowired private IbanAttributeMasterRepository ibanAttributeMasterRepository;

	@Autowired private CodifichePaRepository codifichePaRepository;

	@Autowired private EncodingsService encodingsService;

	@Autowired private ModelMapper modelMapper;

	@Autowired private AzureStorageInteraction azureStorageInteraction;

	public IbanEnhanced createIban(
			@Pattern(regexp = "\\d{11}", message = "CI fiscal code not valid") @NotBlank
			String organizationFiscalCode,
			@Valid @NotNull IbanEnhanced iban) {
		// retrieve the creditor institution and throw exception if not found
		Pa existingCreditorInstitution = getCreditorInstitutionIfExists(organizationFiscalCode);
		// Update Ica Table
		azureStorageInteraction.updateECIcaTable(existingCreditorInstitution.getIdDominio());

		List<CodifichePa> encodings = codifichePaRepository.findAllByFkPa_ObjId(existingCreditorInstitution.getObjId());
		this.checkAndSetup(iban, existingCreditorInstitution, encodings);

		// retrieve an existing iban or generate a new one if not defined
		Iban ibanToBeCreated =
				ibanRepository
				.findByIban(iban.getIbanValue())
				.orElseGet(() -> saveIban(iban, organizationFiscalCode));
		// check if IBAN is a postal IBAN and if it is already related to an existing Creditor
		// Institution
		if (isPostalIban(iban.getIbanValue())
				&& !ibanMasterRepository.findByFkIban(ibanToBeCreated.getObjId()).isEmpty())
			throw new AppException(
					AppError.POSTAL_IBAN_ALREADY_ASSOCIATED,
					iban.getIbanValue(),
					existingCreditorInstitution.getIdDominio());
		// check if IBAN was already associated to creditor institution. If already associated, throw an
		// exception
		getIbanMaster(ibanToBeCreated, existingCreditorInstitution)
		.ifPresent(
				s -> {
					throw new AppException(
							AppError.IBAN_ALREADY_ASSOCIATED,
							iban.getIbanValue(),
							existingCreditorInstitution.getIdDominio());
				});
		IbanMaster ibanCIRelationToBeCreated =
				saveIbanCIRelation(existingCreditorInstitution, iban, ibanToBeCreated);
		// generate the relation between iban and attributes
		List<IbanAttributeMaster> updatedIbanAttributes =
				saveIbanLabelRelation(iban, ibanCIRelationToBeCreated);

		// return final object
		return convertEntitiesToModel(
				existingCreditorInstitution,
				ibanToBeCreated,
				updatedIbanAttributes,
				ibanCIRelationToBeCreated);
	}

	public IbanEnhanced updateIban(
			@NotBlank @Pattern(regexp = "\\d{11}", message = "CI fiscal code not valid")
			String organizationFiscalCode,
			@NotBlank
			@Pattern(regexp = "[a-zA-Z]{2}\\d{2}[a-zA-Z0-9]{1,30}", message = "IBAN code not valid")
			String ibanCode,
			@Valid @NotNull IbanEnhanced iban) {
		if (!ibanCode.equals(iban.getIbanValue())) {
			throw new AppException(
					HttpStatus.BAD_REQUEST,
					"IBAN codes not matching",
					"The IBAN code in the body request does not match with the IBAN code passed as path"
							+ " parameter.");
		}
		// retrieve the creditor institution and throw exception if not found
		Pa existingCreditorInstitution = getCreditorInstitutionIfExists(organizationFiscalCode);
		// Update Ica Table
		azureStorageInteraction.updateECIcaTable(existingCreditorInstitution.getIdDominio());
		List<CodifichePa> encodings = codifichePaRepository.findAllByFkPa_ObjId(existingCreditorInstitution.getObjId());
		this.checkEcodingsAssociation(iban, existingCreditorInstitution, encodings);

		// retrieve the iban and throw exception if not found. If creditor institution is the owner, it
		// can update the IBAN object
		Iban existingIban =
				ibanRepository
				.findByIban(ibanCode)
				.orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND, organizationFiscalCode));
		if (!CommonUtil.toTimestamp(iban.getDueDate()).equals(existingIban.getDueDate())) {
			this.checkDueDate(iban);
		}
		if (organizationFiscalCode.equals(existingIban.getFiscalCode())) {
			existingIban = saveIban(iban, existingIban);
		}
		// check if IBAN was already associated to creditor institution. If not associated, throw an
		// exception
		IbanMaster existingIbanMaster =
				getIbanMaster(existingIban, existingCreditorInstitution)
				.orElseThrow(
						() ->
						new AppException(
								AppError.IBAN_NOT_ASSOCIATED, iban.getIbanValue(), organizationFiscalCode));
		if (!CommonUtil.toTimestamp(iban.getValidityDate()).equals(existingIbanMaster.getValidityDate())) {
			this.checkValidityDate(iban);
		}
		// generate a relation between iban and CI
		IbanMaster ibanCIRelationToBeUpdated =
				saveIbanCIRelation(existingIbanMaster, existingCreditorInstitution, iban, existingIban);
		// remove all labels and save them again
		ibanAttributeMasterRepository.deleteAll(existingIbanMaster.getIbanAttributesMasters());
		ibanAttributeMasterRepository.flush();
		List<IbanAttributeMaster> updatedIbanAttributes =
				saveIbanLabelRelation(iban, ibanCIRelationToBeUpdated);

		// return final object
		return convertEntitiesToModel(
				existingCreditorInstitution,
				existingIban,
				updatedIbanAttributes,
				ibanCIRelationToBeUpdated);
	}

	public IbansEnhanced getCreditorInstitutionsIbansByLabel(
			@NotNull @Pattern(regexp = "\\d{11}", message = "CI fiscal code not valid")
			String organizationFiscalCode,
			String label) {
		List<IbanEnhanced> ibanEnhancedList = new ArrayList<>();
		Optional<Pa> creditorInstitutionOpt = paRepository.findByIdDominio(organizationFiscalCode);
		Pa pa =
				creditorInstitutionOpt.orElseThrow(
						() ->
						new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, organizationFiscalCode));

		List<IbanMaster> ibanMasters = ibanMasterRepository.findByFkPa(pa.getObjId());
		ibanMasters.forEach(
				ibanMaster -> {
					Optional<Iban> ibanOpt = ibanRepository.findById(ibanMaster.getFkIban());
					Iban iban = ibanOpt.orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND));
					Optional<Pa> ciOwnerOpt = paRepository.findByIdDominio(iban.getFiscalCode());
					Pa ciOwner =
							ciOwnerOpt.orElseThrow(
									() ->
									new AppException(
											AppError.CREDITOR_INSTITUTION_NOT_FOUND, iban.getFiscalCode()));

					if (label == null || label.isEmpty()) {
						IbanEnhanced ibanEnhanced =
								convertEntitiesToModel(
										ciOwner, iban, ibanMaster.getIbanAttributesMasters(), ibanMaster);
						ibanEnhancedList.add(ibanEnhanced);
					} else {
						boolean labelMatch =
								ibanMaster.getIbanAttributesMasters().stream()
								.map(
										ibanAttributeMaster ->
										ibanAttributeMaster.getIbanAttribute().getAttributeName())
								.anyMatch(name -> name.equalsIgnoreCase(label));

						if (labelMatch) {
							IbanEnhanced ibanEnhanced =
									convertEntitiesToModel(
											ciOwner, iban, ibanMaster.getIbanAttributesMasters(), ibanMaster);
							ibanEnhancedList.add(ibanEnhanced);
						}
					}
				});

		if(ibanEnhancedList.isEmpty() && (acaLabel.equals(label) || cupLabel.equals(label))) {
			IbanMaster lastPublishedIban = getLastPublishedIban(pa);
			if(lastPublishedIban != null) {
				ibanEnhancedList.add(convertEntitiesToModel(pa, lastPublishedIban.getIban(), lastPublishedIban.getIbanAttributesMasters(), lastPublishedIban));
			}
		}

		return IbansEnhanced.builder().ibanEnhancedList(ibanEnhancedList).build();
	}

	public String deleteIban(
			@NotBlank @Pattern(regexp = "\\d{11}", message = "CI fiscal code not valid")
			String organizationFiscalCode,
			@NotNull
			@Pattern(regexp = "[a-zA-Z]{2}\\d{2}[a-zA-Z0-9]{1,30}", message = "IBAN code not valid")
			String ibanValue) {
		// Get iban entity to be deleted
		Iban ibanToBeDeleted = getIbanIfExists(ibanValue);

		// Get pa entity
		Pa existingCreditorInstitution = getCreditorInstitutionIfExists(organizationFiscalCode);

		// Update Ica Table
		azureStorageInteraction.updateECIcaTable(existingCreditorInstitution.getIdDominio());

		// Get all ibanMaster relations
		List<IbanMaster> ibanMastersToBeDeleted =
				ibanMasterRepository.findByFkIbanAndFkPa(
						ibanToBeDeleted.getObjId(), existingCreditorInstitution.getObjId());

		// Get all ibanAttributesMaster relations
		List<IbanAttributeMaster> ibanAttributeMastersToBeDeleted =
				ibanAttributeMasterRepository.findByFkIbanMasterIn(
						ibanMastersToBeDeleted.stream().map(IbanMaster::getObjId).collect(Collectors.toList()));

		// Delete related encoding if postal iban
		if (isPostalIban(ibanToBeDeleted.getIban())) deleteEncoding(organizationFiscalCode, ibanValue);

		// Delete all relations listed before
		ibanAttributeMasterRepository.deleteAll(ibanAttributeMastersToBeDeleted);
		ibanMasterRepository.deleteAll(ibanMastersToBeDeleted);
		if (ibanMasterRepository.findByFkIban(ibanToBeDeleted.getObjId()).isEmpty()) {
			ibanRepository.delete(ibanToBeDeleted);
		}

		return String.format(
				"\"The Iban %s for the creditor institution %s has been deleted\"",
				ibanValue, organizationFiscalCode);
	}

	public void createMassiveIbans(@NotNull MultipartFile file) {
		// parse the zip file
		BiFunction<InputStream, Boolean, List<CheckItem>> func =
				(inputStream, k) -> {
					try {
						return createIbansByFile(inputStream);
					} catch (IOException e) {
						throw new AppException(
								HttpStatus.BAD_REQUEST, FILE_BAD_REQUEST, "Problem in the file examination", e);
					}
				};
				massiveRead(file, func);
	}

	public IbanLabel upsertIbanLabel(@Valid @NotNull IbanLabel ibanLabel) {
		IbanAttribute ibanAttribute = ibanAttributeRepository.findAll()
				.stream()
				.filter(attribute -> attribute.getAttributeName().equals(ibanLabel.getName()))
				.findFirst()
				.orElse(IbanAttribute.builder()
						.attributeName(ibanLabel.getName())
						.build());
		ibanAttribute.setAttributeDescription(ibanLabel.getDescription());
		ibanAttribute = ibanAttributeRepository.save(ibanAttribute);
		return IbanLabel.builder()
				.name(ibanAttribute.getAttributeName())
				.description(ibanAttribute.getAttributeDescription())
				.build();
	}


    public void createMassiveIbansByCsv(MultipartFile file) {
        try {
            List<IbanMassLoadCsv> x = validateCsv(file);
            List<IbanMassLoadCsv> toInsert = new ArrayList<>();
            List<IbanMassLoadCsv> toDelete = new ArrayList<>();
            List<IbanMassLoadCsv> toUpdate = new ArrayList<>();
            x.forEach(
                ibanMassLoadCsvRow -> {
                    if("I".equalsIgnoreCase(ibanMassLoadCsvRow.getOperazione())) {
                        toInsert.add(ibanMassLoadCsvRow);
                    } else if("D".equalsIgnoreCase(ibanMassLoadCsvRow.getOperazione())) {
                        toDelete.add(ibanMassLoadCsvRow);
                    } else if("U".equalsIgnoreCase(ibanMassLoadCsvRow.getOperazione())) {
                        toUpdate.add(ibanMassLoadCsvRow);
                    } else {
                        throw new AppException(
                                HttpStatus.BAD_REQUEST, FILE_BAD_REQUEST, "Column \"Operazione\" must be equal to either \"I\", \"D\" or \"U\"");
                    }
                }
            );

            IbansMaster ibansMasterToInsert = IbansMaster.builder().build();
            IbansMaster ibansMasterToDelete = IbansMaster.builder().build();
            IbansMaster ibansMasterToUpdate = IbansMaster.builder().build();

            modelMapper.map(IbansMassLoadCsv.builder().ibanRows(toInsert).build(), ibansMasterToInsert);
            modelMapper.map(IbansMassLoadCsv.builder().ibanRows(toDelete).build(), ibansMasterToDelete);
            modelMapper.map(IbansMassLoadCsv.builder().ibanRows(toUpdate).build(), ibansMasterToUpdate);

            this.insertIbans(ibansMasterToInsert.getIbanMasterList());
            this.updateIbans(ibansMasterToUpdate.getIbanMasterList());
            this.deleteIbans(ibansMasterToDelete.getIbanMasterList(), ibansMasterToInsert.getIbanMasterList());
        } catch (IOException | RuntimeException e) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST, FILE_BAD_REQUEST, "Problem in the file examination - " + e.getMessage(), e);
        }
    }
	
	public boolean isPostalIban(String ibanValue) {
		String abiCode = ibanValue.substring(5, 10);
		return abiCode.equals(postalIbanAbi);
	}

    private List<IbanMassLoadCsv> validateCsv(MultipartFile file) throws IOException {
        // read CSV
        Reader reader =
                new StringReader(new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8));

        // create mapping strategy to arrange the column name
        HeaderColumnNameMappingStrategy<IbanMassLoadCsv> mappingStrategy =
                new HeaderColumnNameMappingStrategy<>();
        mappingStrategy.setType(IbanMassLoadCsv.class);

        // execute validation
        CsvToBean<IbanMassLoadCsv> parsedCSV =
                new CsvToBeanBuilder<IbanMassLoadCsv>(reader)
                        .withSeparator(',')
                        .withFieldAsNull(CSVReaderNullFieldIndicator.NEITHER)
                        .withOrderedResults(true)
                        .withMappingStrategy(mappingStrategy)
                        .withType(IbanMassLoadCsv.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .withThrowExceptions(false)
                        .build();

        List<IbanMassLoadCsv> y = parsedCSV.parse();
        List<CsvException> errors = parsedCSV.getCapturedExceptions();

        if (!errors.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            errors.forEach(error -> stringBuilder.append(String.format("|%s |", error.getMessage())));
            throw new AppException(AppError.IBANS_BAD_REQUEST, stringBuilder);
        }
        return y;
    }

	private IbanMaster getLastPublishedIban(Pa pa) {
		List<IbanMaster> activeIbans = pa.getIbanMasters().stream()
				.filter(ibanPa -> ibanPa.getValidityDate().before(Timestamp.valueOf(LocalDateTime.now())))
				.collect(Collectors.toList());
		return activeIbans.stream().max(Comparator.comparing(IbanMaster::getInsertedDate)).orElse(null);
	}

	private Pa getCreditorInstitutionIfExists(String organizationFiscalCode) {
		// retrieve the creditor institution and throw exception if not found
		Optional<Pa> creditorInstitutionOpt = paRepository.findByIdDominio(organizationFiscalCode);
		return creditorInstitutionOpt.orElseThrow(
				() -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, organizationFiscalCode));
	}

	private Optional<IbanMaster> getIbanMaster(Iban iban, Pa creditorInstitution) {
		return ibanMasterRepository
				.findByFkIbanAndFkPa(iban.getObjId(), creditorInstitution.getObjId())
				.stream()
				.findFirst();
	}

	private Iban saveIban(IbanEnhanced iban, String organizationFiscalCode) {
		Iban ibanToBeCreated =
				Iban.builder()
				.iban(iban.getIbanValue())
				.fiscalCode(organizationFiscalCode)
				.dueDate(CommonUtil.toTimestamp(iban.getDueDate()))
				.build();
		return saveIban(iban, ibanToBeCreated);
	}

	private Iban saveIban(IbanEnhanced iban, Iban existingIban) {
		existingIban.setDueDate(CommonUtil.toTimestamp(iban.getDueDate()));
		return ibanRepository.save(existingIban);
	}

	private IbanMaster saveIbanCIRelation(
			Pa creditorInstitution, IbanEnhanced iban, Iban ibanToBeCreated) {
		return saveIbanCIRelation(new IbanMaster(), creditorInstitution, iban, ibanToBeCreated);
	}

	private IbanMaster saveIbanCIRelation(
			IbanMaster ibanMaster, Pa creditorInstitution, IbanEnhanced iban, Iban ibanToBeCreated) {
		ibanMaster.setFkPa(creditorInstitution.getObjId());
		ibanMaster.setFkIban(ibanToBeCreated.getObjId());
		ibanMaster.setIbanStatus(iban.isActive() ? IbanStatus.ENABLED : IbanStatus.DISABLED);
		ibanMaster.setInsertedDate(
				ibanMaster.getInsertedDate() != null
				? ibanMaster.getInsertedDate()
						: CommonUtil.toTimestamp(OffsetDateTime.now(ZoneOffset.UTC)));
		ibanMaster.setValidityDate(CommonUtil.toTimestamp(iban.getValidityDate()));
		ibanMaster.setDescription(iban.getDescription());
		ibanMaster.setPa(creditorInstitution); // setting CI object reference
		ibanMaster.setIban(ibanToBeCreated); // setting IBAN object reference
		return ibanMasterRepository.save(ibanMaster);
	}

	private List<IbanAttributeMaster> saveIbanLabelRelation(
			IbanEnhanced iban, IbanMaster ibanCIRelation) {
		// validating and inserting the labels
		Map<String, IbanAttribute> validLabels =
				ibanAttributeRepository.findAll().stream()
				.collect(Collectors.toMap(IbanAttribute::getAttributeName, obj -> obj));

		List<IbanAttributeMaster> labels = new LinkedList<>();
		// Analyzing the label from IBAN object passed as input (analyze an empty list if passed as
		// null)
		for (IbanLabel label : Optional.ofNullable(iban.getLabels()).orElse(List.of())) {
			/*
       Get the IBAN attribute using the label from list as search key from the valid label map:
        - If found, generate the entity to be saved.
        - If not found (null result), throw an exception and stop computation.
			 */
			IbanAttribute ibanAttribute =
					Optional.ofNullable(validLabels.get(label.getName()))
					.orElseThrow(() -> new AppException(AppError.IBAN_LABEL_NOT_VALID, label.getName()));
			IbanAttributeMaster ibanAttributesMasterToBeCreated =
					IbanAttributeMaster.builder()
					.fkIbanMaster(ibanCIRelation.getObjId())
					.fkAttribute(ibanAttribute.getObjId())
					.ibanMaster(ibanCIRelation)
					.ibanAttribute(ibanAttribute)
					.build();
			labels.add(ibanAttributeMasterRepository.save(ibanAttributesMasterToBeCreated));
		}
		return labels;
	}

	private void deleteEncoding(String organizationFiscalCode, String ibanValue) {
		// check the relation between encoding and CI and delete CI encoding
		@NotNull
		@Valid
		List<Encoding> encodings =
		encodingsService.getCreditorInstitutionEncodings(organizationFiscalCode).getEncodings();
		String econdingToDelete = ibanValue.substring(ibanValue.length() - 12);
		boolean existEncoding =
				encodings.stream()
				.map(Encoding::getEncodingCode)
				.anyMatch(encodingCode -> encodingCode.equals(econdingToDelete));
		if (existEncoding)
			encodingsService.deleteCreditorInstitutionEncoding(organizationFiscalCode, econdingToDelete);
	}

	private IbanEnhanced convertEntitiesToModel(
			Pa creditorInstitution,
			Iban iban,
			List<IbanAttributeMaster> ibanAttributes,
			IbanMaster ibanCIRelation) {
		return IbanEnhanced.builder()
				.companyName(creditorInstitution.getRagioneSociale())
				.ibanValue(iban.getIban())
				.description(ibanCIRelation.getDescription())
				.labels(
						Optional.of(
								ibanAttributes.stream()
								.map(obj -> modelMapper.map(obj, IbanLabel.class))
								.collect(Collectors.toList()))
						.orElse(List.of()))
				.ciOwnerFiscalCode(iban.getFiscalCode())
				.isActive(IbanStatus.ENABLED.equals(ibanCIRelation.getIbanStatus()))
				.validityDate(CommonUtil.toOffsetDateTime(ibanCIRelation.getValidityDate()))
				.publicationDate(CommonUtil.toOffsetDateTime(ibanCIRelation.getInsertedDate()))
				.dueDate(CommonUtil.toOffsetDateTime(iban.getDueDate()))
				.build();
	}

	private Iban getIbanIfExists(String ibanValue) {
		return ibanRepository
				.findByIban(ibanValue)
				.orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND, ibanValue));
	}

	private List<CheckItem> createIbansByFile(InputStream inputStream) throws IOException{
		// map file into model class
		inputStream.reset();
		IbansMassLoad ibansLoaded = CommonUtil.mapJSON(inputStream, IbansMassLoad.class);
		List<CheckItem> checks = verifyIbansLoaded(ibansLoaded);
		Optional<CheckItem> check =
				checks.stream()
				.filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID))
				.findFirst();
		if (check.isPresent()) {
			throw new AppException(
					AppError.IBANS_BAD_REQUEST,
					String.format("[%s] %s", check.get().getValue(), check.get().getNote()));
		}
		IbansMaster ibanMaster = IbansMaster.builder().build();
		modelMapper.map(ibansLoaded, ibanMaster);
		var pa = this.getPaIfExists(ibansLoaded.getCreditorInstitutionCode());
		this.saveIbans(ibanMaster.getIbanMasterList(), pa);
		return checks;
	}

	private List<CheckItem> verifyIbansLoaded(IbansMassLoad ibansLoaded) {
		List<CheckItem> checkItemList = new ArrayList<>();
		String paFiscalCode = ibansLoaded.getCreditorInstitutionCode();
		Pa pa = null;
		List<CodifichePa> encodings = null;
		try {
			// check PA
			pa = getPaIfExists(paFiscalCode);

			// checks if the PA is associated with a qr-code (if this is not the case, the association is created)
			encodings = codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId());
			this.createQrCode(pa, encodings);

		} catch (AppException e) {
			checkItemList.add(
					CheckItem.builder()
					.title("Check QR-CODE")
					.value(paFiscalCode)
					.valid(CheckItem.Validity.NOT_VALID)
					.note(e.getHttpStatus()+" : "+e.getMessage())
					.build());
		}

		if (pa != null) {
			checkItemList.addAll(checkIbans(pa, ibansLoaded.getIbans(), encodings));
		}

		return checkItemList;
	}

	/**
	 * Helper for the massive read
	 *
	 * @param file MultiPartFile to analyze
	 * @param callback function to apply to file
	 * @return a List of massiveChecks corresponding to the zip entry.
	 */
	@java.lang.SuppressWarnings("java:S5042")
	private List<MassiveCheck> massiveRead(
			MultipartFile file,
			BiFunction<InputStream, Boolean, List<CheckItem>> callback) {
		List<MassiveCheck> massiveChecks = new ArrayList<>();
		try {
			// bytes and number of files counter
			var bytesCounter =
					new Object() {
				int totalBytes = 0;
			};
			int nOfZipFiles = 0;

			// function to execute input callback during the analysis of zip files
			BiFunction<InputStream, Integer, List<CheckItem>> callbackCaller =
					(inputStream, nBytesOfEntries) -> {
						bytesCounter.totalBytes += nBytesOfEntries;

						if (bytesCounter.totalBytes > thresholdSize) {
							throw new AppException(
									HttpStatus.BAD_REQUEST, FILE_BAD_REQUEST, "Zip content too large");
						}

						return callback.apply(inputStream, false);
					};

					// unzip the input stream
					ZipInputStream zis = new ZipInputStream(file.getInputStream());
					ZipEntry zipEntry = zis.getNextEntry();
					while (zipEntry != null) {
						++nOfZipFiles;
						if (nOfZipFiles > thresholdEntries) {
							throw new AppException(
									HttpStatus.BAD_REQUEST,
									FILE_BAD_REQUEST,
									"Zip content has too many entries (check for hidden files)");
						}

						List<CheckItem> listToAdd = zipReading(zipEntry, zis, callbackCaller);

						// empty if file is a hidden one or a folder
						if (!listToAdd.isEmpty()) {
							massiveChecks.add(
									MassiveCheck.builder().fileName(zipEntry.getName()).checkItems(listToAdd).build());
						}
						// go to next file inside zip
						zipEntry = zis.getNextEntry();
					}
		} catch (IOException e) {
			throw new AppException(
					HttpStatus.BAD_REQUEST, FILE_BAD_REQUEST, "Problem when unzipping file", e);
		}
		return massiveChecks;
	}

	/**
	 * Open zipEntry content and put it on an outputstream
	 *
	 * @param zipEntry entry of the zipFile
	 * @param zis zip file content
	 * @return a List of checkItems corresponding to the zip entry.
	 */
	// added to avoid sonar warning, we need to use tempFile to avoid to analyze hidden files and
	// directories
	@java.lang.SuppressWarnings({"javasecurity:S6096", "java:S5443"})
	private List<CheckItem> zipReading(
			ZipEntry zipEntry,
			ZipInputStream zis,
			BiFunction<InputStream, Integer, List<CheckItem>> callback)
					throws IOException {
		File tempFile = File.createTempFile(zipEntry.getName(), "");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int nBytes = 0;
		if (!tempFile.isHidden() && !zipEntry.isDirectory()) {
			for (int c = zis.read(); c != -1; c = zis.read()) {
				baos.write(c);
				++nBytes;
			}
			Files.delete(tempFile.toPath());
			return callback.apply(new ByteArrayInputStream(baos.toByteArray()), nBytes);
		}
		// remove temp file
		Files.delete(tempFile.toPath());
		return new ArrayList<>();
	}

	/**
	 * @param creditorInstitutionCode = identificativo Dominio
	 * @return get the PA from DB using identificativoDominio
	 */
	private Pa getPaIfExists(String creditorInstitutionCode) {
		return paRepository
				.findByIdDominio(creditorInstitutionCode)
				.orElseThrow(
						() ->
						new AppException(AppError.IBANS_BAD_REQUEST, creditorInstitutionCode + " not found"));
	}

	private void saveIbans(List<IbanMaster> ibanMasterList,  Pa pa) {	  

		List<IbanMaster> ibanMasterToSaveList = new ArrayList<>();
		List<Iban> ibanToSaveList = new ArrayList<>();

		for (IbanMaster loadedIbanMaster: ibanMasterList) {
			loadedIbanMaster.setPa(pa);
			Iban loadedIban = loadedIbanMaster.getIban();	  
			// check if it is a new iban or an existing iban
			Iban i = ibanRepository.findByIban(loadedIban.getIban()).orElse(null);
			if(null != i) {
				loadedIban.setObjId(i.getObjId());
				// update the properties of the existing iban
				modelMapper.map(loadedIban, i);

				List<IbanMaster> m = ibanMasterRepository.findByFkIbanAndFkPa(i.getObjId(), pa.getObjId());

				if (CollectionUtils.isNotEmpty(m)) {
					// there is only one occurrence for the pa-iban association (unique constraint) --> one element in the list
					loadedIbanMaster.setObjId(m.get(0).getObjId());
					// update the properties of the existing iban master 
					modelMapper.map(loadedIbanMaster, m.get(0));
					loadedIbanMaster = m.get(0);
					loadedIbanMaster.setIban(i);
				}

				loadedIban = i;
			} 
			ibanMasterToSaveList.add(loadedIbanMaster);
			ibanToSaveList.add(loadedIban);
		}

		ibanRepository.saveAll(ibanToSaveList);
		ibanMasterRepository.saveAll(ibanMasterToSaveList);
	}

	/**
	 * @param pa check (and eventually creates) if PA has QR-CODE encodings
	 */
	private void createQrCode(Pa pa, List<CodifichePa> codifichePaList) {
		boolean hasQrcodeEncoding =
				codifichePaList.stream()
				.anyMatch(elem -> elem.getFkCodifica().getIdCodifica().equals("QR-CODE"));

		if (!hasQrcodeEncoding) {
			Encoding encoding =
					Encoding.builder()
					.codeType(CodeTypeEnum.QR_CODE)
					.encodingCode(pa.getIdDominio()) // for type QR-CODE = CF (11 characters - PA fiscal code)
					.build();
			encodingsService.createCreditorInstitutionEncoding(pa.getIdDominio(), encoding);
		}

	}
	
	/**
	 * @param pa check (and eventually creates) if PA has Barcode encodings (only postal ibans) 
	 */

	private void createBarcode(String ibanValue, Pa pa, List<CodifichePa> encodings) {
		String ibanEncoding = ibanValue.substring(ibanValue.length()- 12);
		boolean hasBarcodeEncoding =
				encodings.stream()
				.filter(
						encoding ->
						encoding
						.getFkCodifica()
						.getIdCodifica()
						.equals(Encoding.CodeTypeEnum.BARCODE_128_AIM.getValue())) 
				.anyMatch(encoding -> encoding.getCodicePa().equals(ibanEncoding));

		if (!hasBarcodeEncoding) {
			Encoding encoding =
					Encoding.builder()
					.codeType(CodeTypeEnum.BARCODE_128_AIM)
					.encodingCode(ibanEncoding) // for type BARCODE-128-AIM = CCPost (last 12 characters of postal iban value)
					.build();
			encodingsService.createCreditorInstitutionEncoding(pa.getIdDominio(), encoding);
		}
	}

	private void checkAndSetup(IbanEnhanced iban, Pa existingCreditorInstitution, List<CodifichePa> encodings) {
		checkValidityDate(iban);
		checkDueDate(iban);
		checkEcodingsAssociation(iban, existingCreditorInstitution, encodings);
	}

	private void checkValidityDate(IbanEnhanced iban) {
		// check validity date
		CheckItem check = CommonUtil.checkValidityDate(iban.getValidityDate().toLocalDateTime());
		if (check.getValid().equals(Validity.NOT_VALID)) {
			throw new AppException(
					HttpStatus.BAD_REQUEST, check.getTitle(), check.getNote() + check.getValue());
		}
	}

	private void checkDueDate(IbanEnhanced iban) {
		CheckItem check;
		// check due date
		check = CommonUtil.checkDueDate(iban.getValidityDate().toLocalDateTime(), iban.getDueDate().toLocalDateTime());
		if (check.getValid().equals(Validity.NOT_VALID)) {
			throw new AppException(
					HttpStatus.BAD_REQUEST, check.getTitle(), check.getNote() + check.getValue());
		}
	}

	private void checkEcodingsAssociation(IbanEnhanced iban, Pa existingCreditorInstitution,
			List<CodifichePa> encodings) {
		// checks the PA is associated with a qr-code (if this is not the case, the association is created)
		this.createQrCode(existingCreditorInstitution, encodings);
		if (isPostalIban(iban.getIbanValue())) {
			// check and if it doesn't exist create BARCODE_128_AIM encoding
			this.createBarcode(iban.getIbanValue(), existingCreditorInstitution, encodings);
		}
	}

	private List<CheckItem> checkIbans(Pa pa, 
			List<it.gov.pagopa.apiconfig.core.model.massiveloading.Iban> ibansLoaded, List<CodifichePa> encodings) {
		
		List<CheckItem> checkItemList = new ArrayList<>();		
		ibansLoaded.forEach(item -> checkItemList.addAll(this.checkSingleIban(pa, encodings, item)));
		return checkItemList;
	}

	private List<CheckItem> checkSingleIban(Pa pa, List<CodifichePa> encodings,
			it.gov.pagopa.apiconfig.core.model.massiveloading.Iban item) {
		List<CheckItem> checkItemList = new ArrayList<>();
		LocalDateTime validityDate = LocalDateTime.from(DateTimeFormatter.ofPattern(CommonUtil.DATE_FORMAT_PATTERN).parse(item.getValidityDate()));
		LocalDateTime dueDate = LocalDateTime.from(DateTimeFormatter.ofPattern(CommonUtil.DATE_FORMAT_PATTERN).parse(item.getDueDate()));
		// check validity date
		checkItemList.add(CommonUtil.checkValidityDate(validityDate));
		// check due date
		checkItemList.add(CommonUtil.checkDueDate(validityDate, dueDate));
		// check iban
		checkItemList.add(getIbanCheck(pa, item.getIbanValue(), encodings));
		return checkItemList;
	}

	private CheckItem getIbanCheck(Pa pa,
			String iban, List<CodifichePa> encodings) {
		boolean valid = IBANValidator.getInstance().isValid(iban);
		String note = valid ? "":"Iban not valid";
		String action = "";

		// check if postal Iban
		if (valid && this.isPostalIban(iban)) {
			
			Optional<Iban> ibanEntityToCheck = ibanRepository.findByIban(iban);
			
			// if postal IBAN --> it can be associated with only one Creditor Institution  
			if (ibanEntityToCheck.isPresent()  
					&& !ibanEntityToCheck.get().getIbanMasters().isEmpty() // check that it is not an orphan iban (ie without associations in the iban_master table)
					&& ibanEntityToCheck.get().getIbanMasters().stream().noneMatch(im -> im.getFkPa().equals(pa.getObjId()))) {
				valid = false;
				note = "Postal iban ["+iban+"] already associated with another Creditor Institution. ";
				action = "Change the IBAN or change the Creditor Institution to which it has been associated. ";
			}

			// check and if it doesn't exist create BARCODE_128_AIM encoding
			try {
				this.createBarcode(iban, pa, encodings);
			} catch (AppException e) {
				valid = false;
				note = e.getHttpStatus()+" : "+e.getMessage();
			}
		}

		return CheckItem.builder()
				.title("Iban")
				.value(iban)
				.valid(valid ? CheckItem.Validity.VALID : CheckItem.Validity.NOT_VALID)
				.note(note)
				.action(action)
				.build();
	}

	
	private void insertIbans(List<IbanMaster> ibanMasterList) {
        List<Iban> ibanToInsertList = new ArrayList<>();
        List<IbanMaster> ibanMasterToInsertList = new ArrayList<>();
        for(IbanMaster loadedIbanMaster : ibanMasterList) {
        	Iban iban = loadedIbanMaster.getIban();
        	Pa pa = this.getPaIfExists(iban.getFiscalCode());
        	Iban ibanEntityToCheck = ibanRepository.findByIban(iban.getIban()).orElse(null);
        	// if the iban does not already exist or if it is associated with another PA --> it's inserted
            if(null == ibanEntityToCheck || 
            		ibanEntityToCheck.getIbanMasters().isEmpty() || // check that it is an orphan iban (ie without associations in the iban_master table)
            		ibanEntityToCheck.getIbanMasters().stream().noneMatch(im -> im.getFkPa().equals(pa.getObjId()))) {
            	this.validateIban(loadedIbanMaster, iban, pa);
    			// set the necessary info for persistence
            	loadedIbanMaster.setPa(pa);
            	// before add check that the iban is not already present in the list (two or more rows with the same iban in the file) or table
            	if (ibanToInsertList.stream().filter(i -> i.getIban().equals(iban.getIban())).findAny().isEmpty() && null == ibanEntityToCheck) {
            		ibanToInsertList.add(iban);
            	} else if (null != ibanEntityToCheck) {
            		// if the iban already existed in table associated with another PA --> only create the new relationship on iban_master with the new PA
            		loadedIbanMaster.setIban(ibanEntityToCheck);
            	}
                ibanMasterToInsertList.add(loadedIbanMaster);
            } else {
            	throw new AppException(AppError.IBAN_ALREADY_EXIST, iban.getIban());
            }
        }
        // save Iban and IbanMaster entity
        manageIbanMasterList(ibanMasterToInsertList, ibanRepository.saveAll(ibanToInsertList));
    }

    private void updateIbans(List<IbanMaster> ibanMasterList) {
    	List<IbanMaster> ibanMasterToUpdateList = new ArrayList<>();
    	for(IbanMaster loadedIbanMaster : ibanMasterList) {
    		Iban iban = loadedIbanMaster.getIban();
    		Pa pa = this.getPaIfExists(iban.getFiscalCode());
    		this.validateIban(loadedIbanMaster, iban, pa);
    		loadedIbanMaster.setPa(pa);
    		// checks if the iban exists and if so updates the information
    		Iban ibanToUpdate = ibanRepository.findByIban(iban.getIban()).orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND, iban.getIban()));
    		loadedIbanMaster.getIban().setObjId(ibanToUpdate.getObjId());
			// update the properties of the existing iban
			modelMapper.map(loadedIbanMaster.getIban(), ibanToUpdate);
			
			List<IbanMaster> m = ibanMasterRepository.findByFkIbanAndFkPa(ibanToUpdate.getObjId(), pa.getObjId());

			if (CollectionUtils.isNotEmpty(m)) {
				// there is only one occurrence for the pa-iban association (unique constraint) --> one element in the list
				loadedIbanMaster.setObjId(m.get(0).getObjId());
				// update the properties of the existing iban master 
				modelMapper.map(loadedIbanMaster, m.get(0));
				loadedIbanMaster = m.get(0);
				loadedIbanMaster.setIban(ibanToUpdate);
				
				ibanMasterToUpdateList.add(loadedIbanMaster);
			} else {
				throw new AppException(AppError.IBAN_NOT_ASSOCIATED, iban.getIban(), iban.getFiscalCode());
			}
    		
    	}
    	ibanMasterRepository.saveAll(ibanMasterToUpdateList);
    }

    private void deleteIbans(List<IbanMaster> ibanMasterDeleteList, List<IbanMaster> ibanMasterInsertList) {
        List<Long> ibanToDeleteList = new ArrayList<>();
        List<Long> ibanMasterIdToDeleteList = new ArrayList<>();
        List<Long> ibanAttributeMasterToDeleteList = new ArrayList<>();
        for(IbanMaster loadedIbanMaster : ibanMasterDeleteList) {
        	Iban iban = loadedIbanMaster.getIban();
        	// checks if the iban exists
    		Iban ibanToDelete = ibanRepository.findByIban(iban.getIban()).orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND, iban.getIban()));
    		
    		// before add check:
    		// 1. the iban is not already present in the list (two or more delete rows in the file with the same iban)
    		// 2. the same iban is not present in the insert list 
    		// 3. has only one relationship in the iban_master table
    		if (ibanToDeleteList.stream().filter(i -> ibanToDelete.getObjId().equals(i)).findAny().isEmpty()
    				&& ibanMasterInsertList.stream().filter(ins -> ins.getIban().getIban().equals(ibanToDelete.getIban())).findAny().isEmpty()
    				&& null != ibanToDelete.getIbanMasters() 
    				&& (ibanToDelete.getIbanMasters().isEmpty() || ibanToDelete.getIbanMasters().size() == 1)){
    			ibanToDeleteList.add(ibanToDelete.getObjId());
    		}
    		// delete the relation in the iban_master
    		Pa pa = this.getPaIfExists(iban.getFiscalCode());	
    		// there is only one occurrence for the pa-iban association (unique constraint) --> one element in the list
    		List<IbanMaster> m = ibanMasterRepository.findByFkIbanAndFkPa(ibanToDelete.getObjId(), pa.getObjId());
    		if (CollectionUtils.isEmpty(m)) {throw new AppException(AppError.IBAN_NOT_ASSOCIATED, iban.getIban(), iban.getFiscalCode());}
    		ibanMasterIdToDeleteList.add(m.get(0).getObjId());
    		List<IbanAttributeMaster> ibanAttributeMasterList = m.get(0).getIbanAttributesMasters() != null? m.get(0).getIbanAttributesMasters() : new ArrayList<>();
    		ibanAttributeMasterToDeleteList.addAll (ibanAttributeMasterList.stream()
    				.map(IbanAttributeMaster::getObjId).collect(Collectors.toList()));
    		
        }
        ibanAttributeMasterRepository.deleteByIds(ibanAttributeMasterToDeleteList);
        ibanMasterRepository.deleteByIds(ibanMasterIdToDeleteList);
        ibanRepository.deleteByIds(ibanToDeleteList);
    }

    
    private void manageIbanMasterList(List<IbanMaster> ibanMasterToSaveList, List<Iban> savedIbanList) {
    	for (Iban savedIban: savedIbanList) {
    		ibanMasterToSaveList.forEach(master -> {
    		    if (master.getIban().getIban().equalsIgnoreCase(savedIban.getIban())) {
    		    	// when find the record update the iban with the one saved in the database  
    		        master.setIban(savedIban);
    		    }
    		});
    	}
        ibanMasterRepository.saveAll(ibanMasterToSaveList);
    }
    
    private void validateIban(IbanMaster loadedIbanMaster, Iban iban, Pa pa) {
		// checks if the PA is associated with a qr-code (if this is not the case, the association is created)
		List<CodifichePa> encodings = codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId());
		this.createQrCode(pa, encodings);
		it.gov.pagopa.apiconfig.core.model.massiveloading.Iban ibanToCheck = it.gov.pagopa.apiconfig.core.model.massiveloading.Iban.builder()
		.dueDate(new SimpleDateFormat(CommonUtil.DATE_FORMAT_PATTERN).format(iban.getDueDate()))
		.validityDate(new SimpleDateFormat(CommonUtil.DATE_FORMAT_PATTERN).format(loadedIbanMaster.getValidityDate()))
		.ibanValue(iban.getIban())
		.build();
		// validate the iban
		this.checkSingleIban(pa, encodings, ibanToCheck).stream()
		.filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID))
		.findAny().ifPresent(check -> {throw new AppException(
			AppError.IBANS_BAD_REQUEST,
			String.format("[%s] %s", check.getValue(), check.getNote()));});
	}
}
