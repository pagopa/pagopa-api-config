package it.gov.pagopa.apiconfig.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
import org.xml.sax.SAXException;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.CheckItem;
import it.gov.pagopa.apiconfig.core.model.MassiveCheck;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding.CodeTypeEnum;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanEnhanced;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanLabel;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbansEnhanced;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IcaXml;
import it.gov.pagopa.apiconfig.core.model.massiveloading.IbansMassLoad;
import it.gov.pagopa.apiconfig.core.scheduler.storage.AzureStorageInteraction;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import it.gov.pagopa.apiconfig.starter.entity.Iban;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttribute;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttributeMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanValidiPerPa;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster.IbanStatus;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.repository.CodifichePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanValidiPerPaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;

@Service
@Validated
@Transactional
public class IbanService {

	public static final String IBANS_BAD_REQUEST = "Bad request for the massive loading of IBANs";
	public static final String ACTION_KEY = "action";
	public static final String NOTE_KEY = "note";

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

	@Autowired private IbanValidiPerPaRepository ibanValidiPerPaRepository;

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
		// create encoding for postal iban
		if (isPostalIban(iban.getIbanValue())) {
			String ibanValue = iban.getIbanValue();
			Encoding encoding =
					Encoding.builder()
					.codeType(CodeTypeEnum.BARCODE_128_AIM)
					.encodingCode(
							ibanValue.substring(
									ibanValue.length()
									- 12)) // for BARCODE-128-AIM encoding code equals to last 12 characters
					// of iban value
					.build();

			encodingsService.createCreditorInstitutionEncoding(
					existingCreditorInstitution.getIdDominio(), encoding);
		}

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
		// retrieve the iban and throw exception if not found. If creditor institution is the owner, it
		// can update the IBAN object
		Iban existingIban =
				ibanRepository
				.findByIban(ibanCode)
				.orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND, organizationFiscalCode));
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

		if(ibanEnhancedList.isEmpty() && (label.equals(acaLabel) || label.equals(cupLabel))) {
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

	public boolean isPostalIban(String ibanValue) {
		String abiCode = ibanValue.substring(5, 10);
		return abiCode.equals(postalIbanAbi);
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

	public void createMassiveIbans(@NotNull MultipartFile file) {
		BiFunction<InputStream, Boolean, List<CheckItem>> func =
				(inputStream, k) -> {
					try {
						return createIbansByFile(inputStream, false);
					} catch (IOException | SAXException e) {
						throw new AppException(
								HttpStatus.BAD_REQUEST, IBANS_BAD_REQUEST, "Problem in the file examination", e);
					}
				};
				massiveRead(file, func, false);
	}

	private List<CheckItem> createIbansByFile(InputStream inputStream, boolean force) throws SAXException, IOException{
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
		ArrayList<IbanMaster> ibanMasterList = new ArrayList<>();
		modelMapper.map(ibansLoaded, ibanMasterList);
		var pa = this.getPaIfExists(ibansLoaded.getCreditorInstitutionCode());
		this.saveIbans(ibanMasterList, pa);
		return checks;
	}

	private List<CheckItem> verifyIbansLoaded(IbansMassLoad ibansLoaded) {
		List<CheckItem> checkItemList = new ArrayList<>();
		// check PA
		String paFiscalCode = ibansLoaded.getCreditorInstitutionCode();
		Pa pa = null;
		List<CodifichePa> encodings = null;
		try {
			pa = getPaIfExists(paFiscalCode);

			// check qr-code
			encodings = codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId());
			checkItemList.add(checkQrCode(pa, encodings));

		} catch (AppException e) {
			checkItemList.add(
					CheckItem.builder()
					.title("CI fiscal Code")
					.value(paFiscalCode)
					.valid(CheckItem.Validity.NOT_VALID)
					.note("CI fiscal code not consistent")
					.build());
		}

		if (pa != null) {
			// retrieve CI ibans
			//List<IbanValidiPerPa> ibansPA = ibanValidiPerPaRepository.findAllByFkPa(pa.getObjId());
			checkItemList.addAll(checkIbans(pa, ibansLoaded.getIbans(), pa.getIbans(), encodings));
		}

		return checkItemList;
	}

	/**
	 * Helper for the massive read
	 *
	 * @param file MultiPartFile to analyze
	 * @param callback function to apply to file
	 * @param force boolean to bypass date verification
	 * @return a List of massiveChecks corresponding to the zip entry.
	 */
	@java.lang.SuppressWarnings("java:S5042")
	private List<MassiveCheck> massiveRead(
			MultipartFile file,
			BiFunction<InputStream, Boolean, List<CheckItem>> callback,
			boolean force) {
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
									HttpStatus.BAD_REQUEST, IBANS_BAD_REQUEST, "Zip content too large");
						}

						return callback.apply(inputStream, force);
					};

					// unzip the input stream
					ZipInputStream zis = new ZipInputStream(file.getInputStream());
					ZipEntry zipEntry = zis.getNextEntry();
					while (zipEntry != null) {
						++nOfZipFiles;
						if (nOfZipFiles > thresholdEntries) {
							throw new AppException(
									HttpStatus.BAD_REQUEST,
									IBANS_BAD_REQUEST,
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
					HttpStatus.BAD_REQUEST, IBANS_BAD_REQUEST, "Problem when unzipping file", e);
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
		File tempFile = File.createTempFile(zipEntry.getName(), "xml");
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

	private void saveIbans(ArrayList<IbanMaster> ibanMasterList,  Pa pa) {	  

		ArrayList<IbanMaster> ibanMasterToSaveList = new ArrayList<>();
		ArrayList<Iban> ibanToSaveList = new ArrayList<>();

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
	 * @param pa check if PA has QR-CODE encodings
	 * @param codifichePaList
	 */
	private CheckItem checkQrCode(Pa pa, List<CodifichePa> codifichePaList) {
		boolean hasQrcodeEncoding =
				codifichePaList.stream()
				.anyMatch(elem -> elem.getFkCodifica().getIdCodifica().equals("QR-CODE"));

		return CheckItem.builder()
				.title("QR Code")
				.value(pa.getIdDominio())
				.valid(hasQrcodeEncoding ? CheckItem.Validity.VALID : CheckItem.Validity.NOT_VALID)
				.note(hasQrcodeEncoding ? "QR-Code already exists" : "QR-Code not present")
				.action(hasQrcodeEncoding ? "" : "ADD_QRCODE")
				.build();
	}

	private List<CheckItem> checkIbans(Pa pa, 
			List<it.gov.pagopa.apiconfig.core.model.massiveloading.Iban> ibansLoaded,
			List<IbanValidiPerPa> ibansPA, List<CodifichePa> encodings) {
		List<CheckItem> checkItemList = new ArrayList<>();
		
		ibansLoaded.forEach(item -> {
			    LocalDateTime validityDate = LocalDateTime.from(DateTimeFormatter.ofPattern(CommonUtil.ISO_DATE_FORMAT_ZERO_OFFSET).parse(item.getValidityDate()));
			    LocalDateTime dueDate = LocalDateTime.from(DateTimeFormatter.ofPattern(CommonUtil.ISO_DATE_FORMAT_ZERO_OFFSET).parse(item.getDueDate()));
			    // check validity date
		        checkItemList.add(CommonUtil.checkValidityDate(validityDate));
		        // check due date
		        checkItemList.add(CommonUtil.checkDueDate(validityDate, dueDate));
		        // check iban
				checkItemList.add(getIbanCheckItem(pa, item.getIbanValue(), ibansPA, encodings));
			}
		);
		return checkItemList;
	}

	private CheckItem getIbanCheckItem(Pa pa,
			String iban, List<IbanValidiPerPa> ibansPA, List<CodifichePa> encodings) {
		boolean valid = IBANValidator.getInstance().isValid(iban);
		String note = "Iban not valid";
		String action = null;

		if (valid) {
			
			// check if postal Iban
			if (iban.substring(5, 10).equals(postalIbanAbi)) {
				// check that, if postal IBAN, then is not already associated with another Creditor Institution
				List<IbanValidiPerPa> ibans = ibanValidiPerPaRepository.findAllByIbanAccreditoContainsIgnoreCase(iban);
				if (!ibans.isEmpty()) {
					note = "Postal iban already associated with another Creditor Institution. ";
					action = "Change the IBAN or change the Creditor Institution to which it has been associated. ";
				}
			}
			
			
			/*
			// check if iban is already been added
			boolean found = ibansPA.stream().anyMatch(i -> i.getIbanAccredito().equals(iban));
			if (found) {
				note = "Iban already added. ";

				String abiCode = iban.substring(5, 10);
				if (abiCode.equals(postalIbanAbi)) {
					Map<String, String> result = checkPostalCode(iban.substring(15), encodings);
					note += result.get(NOTE_KEY);
					action = result.get(ACTION_KEY);
				}
			} else {
				String abiCode = iban.substring(5, 10);
				// check if postal iban and then check that the related barcode-128-aim encoding exists
				note = "New Iban. ";
				if (abiCode.equals(postalIbanAbi)) {
					Map<String, String> result = checkPostalCode(iban.substring(15), encodings);
					note += result.get(NOTE_KEY);
					action = result.get(ACTION_KEY);
				}
			}
			*/
		} 
		
		return CheckItem.builder()
				.title("Iban")
				.value(iban)
				.valid(valid ? CheckItem.Validity.VALID : CheckItem.Validity.NOT_VALID)
				.note(note)
				.action(action)
				.build();
	}

	private Map<String, String> checkPostalCode(String ibanEncoding, List<CodifichePa> encodings) {
		boolean encodingFound =
				encodings.stream()
				.filter(
						encoding ->
						encoding
						.getFkCodifica()
						.getIdCodifica()
						.equals(Encoding.CodeTypeEnum.BARCODE_128_AIM.getValue()))
				.anyMatch(encoding -> encoding.getCodicePa().equals(ibanEncoding));
		Map<String, String> result = new HashMap<>();
		result.put(ACTION_KEY, encodingFound ? "" : "ADD_ENCODING");
		result.put(NOTE_KEY, encodingFound ? "Encoding already present." : "Encoding not found.");
		return result;
	}
}
