package io.cloudflight.jems.server.nuts.service

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
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.io.File
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

        val nuts = extractNutsFromDatasets()
        val csvFileName: String = getCsvFileName(nutsFile = nuts.get("files")
            ?: throw ResourceNotFoundException("nuts"))
        val groupedByCodeLength = groupNutsByLevel(getTemporaryCsvFile(csvFileName))

        nutsRepository.saveAll(
            groupedByCodeLength.get(COUNTRY_CODE_LENGTH)?.map { it.toNutsCountry() }!!
        )
        nutsRegion1Repository.saveAll(
            groupedByCodeLength.get(REGION_1_CODE_LENGTH)?.map { it.toNutsRegion1() }!!
        )
        nutsRegion2Repository.saveAll(
            groupedByCodeLength.get(REGION_2_CODE_LENGTH)?.map { it.toNutsRegion2() }!!
        )
        nutsRegion3Repository.saveAll(
            groupedByCodeLength.get(REGION_3_CODE_LENGTH)?.map { it.toNutsRegion3() }!!
        )

        val metadata = nutsMetadataRepository.save(createMetadataFromNuts(nuts)).toOutputNutsMetadata()
        auditService.logEvent(nutsDownloadSuccessful(metadata))
        return metadata
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

    private fun extractNutsFromDatasets(): LinkedHashMap<String, String> {
        val datasets = (JSONParser(restTemplate.getForObject(
            "$GISCO_NUTS_URL/$GISCO_DATASETS_FILE",
            String::class.java)).parse()) as LinkedHashMap<String, *>
        val nutsKeys = datasets.keys.distinct()
        val lastNutKey = nutsKeys.get(nutsKeys.size - 1)
        return datasets[lastNutKey] as LinkedHashMap<String, String>
    }

    private fun getCsvFileName(nutsFile: String): String {
        val nutFile = (JSONParser(restTemplate.getForObject(
            "$GISCO_NUTS_URL/$nutsFile",
            String::class.java)).parse()) as LinkedHashMap<String, *>

        return (nutFile["csv"] as LinkedHashMap<String, String>).values.iterator().next()
    }

    private fun getTemporaryCsvFile(csvFileName: String): File {
        return restTemplate.execute<File>(
            "$GISCO_NUTS_URL/$csvFileName",
            HttpMethod.GET,
            null,
            csvFileExtractor
        ) ?: throw ResourceNotFoundException(csvFileName)
    }

    private fun groupNutsByLevel(csvFile: File): Map<Int, List<Pair<String, String>>> {
        var groupedByCodeLength: Map<Int, List<Pair<String, String>>>? = null
        csvReader().open(csvFile) {
            groupedByCodeLength = readAllWithHeaderAsSequence().map { row: Map<String, String> ->
                if (row[NUTS_ID].isNullOrEmpty() || row[NUTS_NAME].isNullOrEmpty())
                    throw I18nValidationException(i18nKey = "nuts.unable.to.locate.$NUTS_ID.or.$NUTS_NAME")
                else
                    Pair(row[NUTS_ID]!!, row[NUTS_NAME]!!.removeLineBreaks())
            }.groupBy({ it.first.length }, { it })
        }
        return groupedByCodeLength ?: throw ResourceNotFoundException("nuts")
    }

    private fun createMetadataFromNuts(nuts: LinkedHashMap<String, String>): NutsMetadata {
        val date: LocalDate = LocalDate.parse(nuts["date"], DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val title: String = nuts["title"] ?: throw I18nValidationException(httpStatus = HttpStatus.UNPROCESSABLE_ENTITY)

        return NutsMetadata(nutsDate = date, nutsTitle = title)
    }

}
