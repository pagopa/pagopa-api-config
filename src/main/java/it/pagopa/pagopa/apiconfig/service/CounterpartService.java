package it.pagopa.pagopa.apiconfig.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import it.pagopa.pagopa.apiconfig.entity.BinaryFile;
import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTable;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTables;
import it.pagopa.pagopa.apiconfig.model.CounterpartTable;
import it.pagopa.pagopa.apiconfig.model.CounterpartTables;
import it.pagopa.pagopa.apiconfig.model.InformativaContoAccredito;
import it.pagopa.pagopa.apiconfig.repository.BinaryFileRepository;
import it.pagopa.pagopa.apiconfig.repository.InformativePaMasterRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class CounterpartService {

    @Autowired
    InformativePaMasterRepository informativePaMasterRepository;

    @Autowired
    BinaryFileRepository binaryFileRepository;

    @Autowired
    ModelMapper modelMapper;

    public CounterpartTables getCounterpartTables(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<InformativePaMaster> page = informativePaMasterRepository.findAll(pageable);
        return CounterpartTables.builder()
                .counterpartTableList(getCounterpartList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }


    public byte[] getCounterpartTable(@NotNull String idCounterpartTable, @NotNull String creditorInstitutionCode) {
        InformativePaMaster result = getInformativePaMasterIfExists(idCounterpartTable, creditorInstitutionCode);
        return result.getFkBinaryFile().getFileContent();
    }

    public void uploadCounterpartTable(MultipartFile file) {
        try {
            BinaryFile binaryFile = binaryFileRepository.save(BinaryFile.builder()
                    .fileContent(file.getBytes())
                    .fileSize(file.getSize())
                    .fileHash(file.getBytes())
                    // TODO .fileHash() and others?
                    .build());
            XmlMapper xmlMapper = new XmlMapper();
            InformativaContoAccredito informativaContoAccredito = xmlMapper.readValue(file.getBytes(), InformativaContoAccredito.class);
            informativePaMasterRepository.save(InformativePaMaster.builder()
                    .fkBinaryFile(binaryFile)
                    // TODO: parse the file and
                    .dataInizioValidita(CommonUtil.toTimestamp(informativaContoAccredito.getDataInizioValidita()))
                    .build());
        } catch (IOException e) {
            throw new AppException(HttpStatus.BAD_REQUEST, "File Error", "Content of the file not readable", e);
        }

    }

    public void deleteCounterpartTable(@NotEmpty String idCounterpartTable, @NotEmpty String creditorInstitutionCode) {
        InformativePaMaster result = getInformativePaMasterIfExists(idCounterpartTable, creditorInstitutionCode);
        binaryFileRepository.delete(result.getFkBinaryFile());
        // TODO delete INFORMATIVE_PA_DETAIL and FASCE?
        informativePaMasterRepository.delete(result);
    }


    /**
     * @param idCounterpartTable      ID of a counterpart table
     * @param creditorInstitutionCode Creditor institution code
     * @return search on DB and return the {@link InformativePaMaster} if it is present
     * @throws AppException if not found
     */
    private InformativePaMaster getInformativePaMasterIfExists(String idCounterpartTable, String creditorInstitutionCode) {
        Optional<InformativePaMaster> result = informativePaMasterRepository.findByIdInformativaPaAndFkPa_IdDominio(idCounterpartTable, creditorInstitutionCode);
        if (result.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND, "Counterpart Table not found", "No Counterpart Table found with the provided IDs");
        }
        return result.get();
    }

    /**
     * Maps InformativePaMaster objects stored in the DB in a List of CounterpartTable
     *
     * @param page page of {@link InformativePaMaster} returned from the database
     * @return a list of {@link CounterpartTable}.
     */
    private List<CounterpartTable> getCounterpartList(Page<InformativePaMaster> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, CounterpartTable.class))
                .collect(Collectors.toList());
    }
}
