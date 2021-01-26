package io.cloudflight.jems.server.nuts.service

import com.github.doyaaaaaken.kotlincsv.client.CsvFileReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.nuts.dto.OutputNutsMetadata
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.nuts.entity.NutsMetadata
import io.cloudflight.jems.server.nuts.repository.NutsCountryRepository
import io.cloudflight.jems.server.nuts.repository.NutsMetadataRepository
import io.cloudflight.jems.server.nuts.repository.NutsRegion1Repository
import io.cloudflight.jems.server.nuts.repository.NutsRegion2Repository
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.audit.service.AuditService
import org.apache.tomcat.util.json.JSONParser
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class NutsServiceImpl(
    restTemplateBuilder: RestTemplateBuilder,
    private val nutsRepository: NutsCountryRepository,
    private val nutsRegion1Repository: NutsRegion1Repository,
    private val nutsRegion2Repository: NutsRegion2Repository,
    private val nutsRegion3Repository: NutsRegion3Repository,
    private val nutsMetadataRepository: NutsMetadataRepository,
    private val auditService: AuditService
) : NutsService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    val restTemplate: RestTemplate = restTemplateBuilder.build()

    @Transactional(readOnly = true)
    override fun getNutsMetadata(): OutputNutsMetadata? {
        return nutsMetadataRepository.findById(1L)
            .map { it.toOutputNutsMetadata() }
            .map { if (it.date == null && it.title == null) null else it }
            .orElse(null)
    }

    @Transactional
    override fun downloadLatestNutsFromGisco(): OutputNutsMetadata {
        auditService.logEvent(nutsDownloadRequest())

        if (getNutsMetadata() != null)
            throw I18nValidationException(
                i18nKey = "nuts.already.downloaded"
            )

        importNutsFromCsv(stream = openStaticCsvFile(STATIC_DATASETS_FILE))
        log.info("Imported data from static '$STATIC_DATASETS_FILE' file")

        val giscoNutsData = extractNutsFromGiscoDatasets(url = GISCO_DATASETS_URL)
        val giscoCsvFileName: String = getCsvFileName(
            nutsFile = giscoNutsData.get("files") ?: throw ResourceNotFoundException("nuts")
        )
        log.info("Nuts CSV file downloaded from GISCO $GISCO_DATASETS_URL")

        importNutsFromCsv(csvFile = retrieveAndOpenCsvFileFromGisco(giscoCsvFileName))
        log.info("Imported data from GISCO file '${giscoNutsData.get("files")}'")

        val metadata = nutsMetadataRepository.save(createMetadataFromNuts(giscoNutsData)).toOutputNutsMetadata()
        auditService.logEvent(nutsDownloadSuccessful(metadata))
        return metadata
    }

    private fun importNutsFromCsv(csvFile: File) {
        var nutsGroupedByCodeLength: Map<Int, List<Pair<String, String>>>? = null
        csvReader().open(csvFile) { nutsGroupedByCodeLength = groupNutsByLevel(this) }
        importNuts(nutsGroupedByCodeLength)
    }

    private fun importNutsFromCsv(stream: InputStream) {
        var nutsGroupedByCodeLength: Map<Int, List<Pair<String, String>>>? = null
        csvReader().open(stream) { nutsGroupedByCodeLength = groupNutsByLevel(this) }
        importNuts(nutsGroupedByCodeLength)
    }

    private fun importNuts(nutsGroupedByCodeLength: Map<Int, List<Pair<String, String>>>?) {
        if (nutsGroupedByCodeLength == null)
            throw ResourceNotFoundException("nuts")

        nutsRepository.saveAll(nutsGroupedByCodeLength.getCountries())
        nutsRegion1Repository.saveAll(nutsGroupedByCodeLength.getRegion1Nuts())
        nutsRegion2Repository.saveAll(nutsGroupedByCodeLength.getRegion2Nuts())
        nutsRegion3Repository.saveAll(nutsGroupedByCodeLength.getRegion3Nuts())
    }

    @Transactional(readOnly = true)
    override fun getNuts(): List<OutputNuts> {
        if (getNutsMetadata() == null)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "nuts.not.yet.downloaded"
            )
        return groupNuts(nutsRegion3Repository.findAll()).toOutputNuts()
    }

    private fun extractNutsFromGiscoDatasets(url: String): LinkedHashMap<String, String> {
        val datasets = (JSONParser(restTemplate.getForObject(
            url,
            String::class.java)).parse()) as LinkedHashMap<String, *>
        val nutsKeys = datasets.keys.distinct()
        val lastNutsKey = nutsKeys.get(nutsKeys.size - 1)
        return datasets[lastNutsKey] as LinkedHashMap<String, String>
    }

    private fun getCsvFileName(nutsFile: String): String {
        val nutFile = (JSONParser(restTemplate.getForObject(
            "$GISCO_NUTS_URL/$nutsFile",
            String::class.java)).parse()) as LinkedHashMap<String, *>

        return (nutFile["csv"] as LinkedHashMap<String, String>).values.iterator().next()
    }

    private fun retrieveAndOpenCsvFileFromGisco(csvFileName: String): File {
        return restTemplate.execute<File>(
            "$GISCO_NUTS_URL/$csvFileName",
            HttpMethod.GET,
            null,
            csvFileExtractor
        ) ?: throw ResourceNotFoundException(csvFileName)
    }

    private fun openStaticCsvFile(csvFileLocation: String): InputStream =
        javaClass.classLoader.getResourceAsStream(csvFileLocation) ?: throw ResourceNotFoundException(csvFileLocation)

    private fun groupNutsByLevel(csvFileReader: CsvFileReader): Map<Int, List<Pair<String, String>>> =
        csvFileReader.readAllWithHeaderAsSequence().map { row: Map<String, String> ->
            if (row[NUTS_ID].isNullOrEmpty() || row[NUTS_NAME].isNullOrEmpty())
                throw I18nValidationException(i18nKey = "nuts.unable.to.locate.$NUTS_ID.or.$NUTS_NAME")
            else
                Pair(row[NUTS_ID]!!, row[NUTS_NAME]!!.removeLineBreaks())
        }.groupBy({ it.first.length }, { it })

    private fun createMetadataFromNuts(nuts: LinkedHashMap<String, String>): NutsMetadata {
        val date: LocalDate = LocalDate.parse(nuts["date"], DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val title: String = nuts["title"] ?: throw I18nValidationException(httpStatus = HttpStatus.UNPROCESSABLE_ENTITY)

        return NutsMetadata(nutsDate = date, nutsTitle = title)
    }

}
