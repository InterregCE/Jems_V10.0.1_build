package io.cloudflight.ems.nuts.service

import io.cloudflight.ems.api.nuts.dto.OutputNutsMetadata
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.nuts.entity.NutsMetadata
import io.cloudflight.ems.nuts.repository.NutsCountryRepository
import io.cloudflight.ems.nuts.repository.NutsMetadataRepository
import io.cloudflight.ems.nuts.repository.NutsRegion1Repository
import io.cloudflight.ems.nuts.repository.NutsRegion2Repository
import io.cloudflight.ems.nuts.repository.NutsRegion3Repository
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.AuditService
import org.apache.tomcat.util.json.JSONParser
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
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
    private val auditService: AuditService,
    private val securityService: SecurityService
) : NutsService {

    val restTemplate: RestTemplate = restTemplateBuilder.build()

    @Transactional(readOnly = true)
    override fun getNutsMetadata(): OutputNutsMetadata? {
        val metadata = nutsMetadataRepository.findById(1L).get()

        return if (metadata.nutsDate == null && metadata.nutsTitle == null)
            null
        else
            metadata.toOutputNutsMetadata()
    }

    @Transactional
    override fun downloadLatestNutsFromGisco(): OutputNutsMetadata {
        auditService.logEvent(Audit.nutsDownloadRequest(securityService.currentUser))

        if (getNutsMetadata() != null)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
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
        auditService.logEvent(Audit.nutsDownloadSuccessful(securityService.currentUser, metadata))
        return metadata
    }

    @Transactional(readOnly = true)
    override fun getNuts(): Map<NutsIdentifier, Map<NutsIdentifier, Map<NutsIdentifier, Set<NutsIdentifier>>>> {
        if (getNutsMetadata() == null)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "nuts.not.yet.downloaded"
            )
        return groupNuts(nutsRegion3Repository.findAll())
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
        BufferedReader(FileReader(csvFile)).use { reader ->
            val headers = reader.readLine().splitCsvLine()
            val nutIdIndex = headers.indexOf(NUTS_ID)
            val nutNameIndex = headers.indexOf(NUTS_NAME)

            groupedByCodeLength = reader
                .lineSequence()
                .map { it.splitCsvLine() }
                .groupBy({ it[nutIdIndex].length }, { Pair(it[nutIdIndex], it[nutNameIndex]) })
        }
        return groupedByCodeLength ?: throw ResourceNotFoundException("nuts")
    }

    private fun createMetadataFromNuts(nuts: LinkedHashMap<String, String>): NutsMetadata {
        val date: LocalDate = LocalDate.parse(nuts["date"], DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val title: String = nuts["title"] ?: throw I18nValidationException(httpStatus = HttpStatus.UNPROCESSABLE_ENTITY)

        return NutsMetadata(nutsDate = date, nutsTitle = title)
    }

}
