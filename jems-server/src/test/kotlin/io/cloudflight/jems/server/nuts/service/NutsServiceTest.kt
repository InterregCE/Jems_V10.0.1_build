package io.cloudflight.jems.server.nuts.service

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.nuts.dto.OutputNutsMetadata
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.nuts.entity.NutsCountry
import io.cloudflight.jems.server.nuts.entity.NutsMetadata
import io.cloudflight.jems.server.nuts.entity.NutsRegion1
import io.cloudflight.jems.server.nuts.entity.NutsRegion2
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import io.cloudflight.jems.server.nuts.repository.NutsCountryRepository
import io.cloudflight.jems.server.nuts.repository.NutsMetadataRepository
import io.cloudflight.jems.server.nuts.repository.NutsRegion1Repository
import io.cloudflight.jems.server.nuts.repository.NutsRegion2Repository
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.audit.service.AuditService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import java.io.File
import java.time.LocalDate
import java.util.Optional

class NutsServiceTest {

    private val region1 = NutsRegion1(id = "CO0", title = "CO0 title", country = NutsCountry("CO", "CO title"))
    private val region2A = NutsRegion2(id = "CO0A", title = "CO0A title", region1 = region1)
    private val region2B = NutsRegion2(id = "CO0B", title = "CO0B title", region1 = region1)
    private val regions = setOf(
        NutsRegion3("CO0A0", "CO0A0 title", region2 = region2A),
        NutsRegion3("CO0A1", "CO0A1 title", region2 = region2A),
        NutsRegion3("CO0B1", "CO0B1 title", region2 = region2B)
    )

    @MockK
    lateinit var restTemplate: RestTemplate
    @MockK
    lateinit var restTemplateBuilder: RestTemplateBuilder
    @MockK
    lateinit var nutsCountryRepository: NutsCountryRepository
    @MockK
    lateinit var nutsRegion1Repository: NutsRegion1Repository
    @MockK
    lateinit var nutsRegion2Repository: NutsRegion2Repository
    @MockK
    lateinit var nutsRegion3Repository: NutsRegion3Repository
    @MockK
    lateinit var nutsMetadataRepository: NutsMetadataRepository
    @RelaxedMockK
    lateinit var auditService: AuditService

    lateinit var nutsService: NutsService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { restTemplateBuilder.build() } returns restTemplate
        nutsService = NutsServiceImpl(
            restTemplateBuilder,
            nutsCountryRepository,
            nutsRegion1Repository,
            nutsRegion2Repository,
            nutsRegion3Repository,
            nutsMetadataRepository,
            auditService
        )
    }

    @Test
    fun getNutsMetadata() {
        initMetadata(date = LocalDate.of(2020, 1, 1), title = "2020-01-01")
        assertThat(nutsService.getNutsMetadata())
            .isEqualTo(OutputNutsMetadata(
                date = LocalDate.of(2020, 1, 1),
                title = "2020-01-01"
            ))
    }

    @Test
    fun `getNutsMetadata empty`() {
        initMetadata(date = null, title = null)
        assertNull(nutsService.getNutsMetadata())
    }

    @Test
    fun `download nuts when existing`() {
        initMetadata(date = LocalDate.of(2020, 1, 1), title = "2020-01-01")
        val exception = assertThrows<I18nValidationException> { nutsService.downloadLatestNutsFromGisco() }
        assertThat(exception).isEqualTo(I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = "nuts.already.downloaded"
        ))

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        with(event.captured) {
            assertThat(action).isEqualTo(AuditAction.NUTS_DATASET_DOWNLOAD)
            assertThat(description).isEqualTo("There was an attempt to download NUTS regions from GISCO. Download is starting...")
        }
    }

    @Test
    fun `download nuts`() {
        initMetadata(date = null, title = null)
        every { restTemplate.getForObject(eq("https://gisco-services.ec.europa.eu/distribution/v2/nuts/datasets.json"), String::class.java) } returns
            """
            {
              "nuts-2020": {
                "date": "18/02/2020",
                "files": "our-file.json",
                "title": "NUTS 2020"
              }
            }
            """.trimIndent()
        every { restTemplate.getForObject(eq("https://gisco-services.ec.europa.eu/distribution/v2/nuts/our-file.json"), String::class.java) } returns
            """
            {
              "csv": {
                "NUTS_2020.csv": "csv/NUTS_2020.csv"
              },
              "geojson": null
            }
            """.trimIndent()
        every {
            restTemplate.execute<File>(
                eq("https://gisco-services.ec.europa.eu/distribution/v2/nuts/csv/NUTS_2020.csv"),
                HttpMethod.GET,
                null,
                any()
            )
        } returns File(this::class.java.classLoader.getResource("nuts/NUTS_2020.csv").toURI())

        val countries: MutableList<Iterable<NutsCountry>> = mutableListOf()
        val regions1: MutableList<Iterable<NutsRegion1>> = mutableListOf()
        val regions2: MutableList<Iterable<NutsRegion2>> = mutableListOf()
        val regions3: MutableList<Iterable<NutsRegion3>> = mutableListOf()
        every { nutsCountryRepository.saveAll(capture(countries)) } returnsArgument 0
        every { nutsRegion1Repository.saveAll(capture(regions1)) } returnsArgument 0
        every { nutsRegion2Repository.saveAll(capture(regions2)) } returnsArgument 0
        every { nutsRegion3Repository.saveAll(capture(regions3)) } returnsArgument 0
        every { nutsMetadataRepository.save(any<NutsMetadata>()) } returnsArgument 0

        assertThat(nutsService.downloadLatestNutsFromGisco()).isEqualTo(OutputNutsMetadata(
            title = "NUTS 2020",
            date = LocalDate.of(2020, 2, 18)
        ))

        assertThat(countries.size).isEqualTo(2).overridingErrorMessage("There needs to be 2 imports performed")
        assertThat(countries[1]).containsExactlyElementsOf(
            setOf(NutsCountry(id = "CO", title = "Country Name"))
        )

        val expectedRegion1 = NutsRegion1(
            id = "CO0",
            title = "Some SubCountry        ",
            country = NutsCountry(id = "CO", title = ""))
        val expectedRegion1Extended = NutsRegion1(
            id = "EX0",
            title = "Extended Reg1 Name",
            country = NutsCountry(id = "EX", title = ""))
        assertThat(regions1.reduce { x, y -> x union y }).containsExactlyInAnyOrder(
            expectedRegion1,
            expectedRegion1Extended,
        )

        val region1 = NutsRegion1(
            id = "CO0",
            title = "",
            country = NutsCountry(id = "CO", title = ""))
        val region1Extended = NutsRegion1(
            id = "EX0",
            title = "",
            country = NutsCountry(id = "EX", title = "")
        )
        assertThat(regions2.reduce { x, y -> x union y }).containsExactlyInAnyOrder(
            NutsRegion2(id = "EX0A", title = "Extended Reg2 Name", region1 = region1Extended),
            NutsRegion2(id = "CO01", title = "Country West", region1 = region1),
            NutsRegion2(id = "CO02", title = "Country North", region1 = region1),
            NutsRegion2(id = "CO0E", title = "Country East", region1 = region1)
        )

        assertThat(regions3.reduce { x, y -> x union y }).containsExactlyInAnyOrder(
            NutsRegion3(id = "EX0A1", title = "Extended Reg3 Name", region2 = getRegion2("EX0A", region1Extended)),
            NutsRegion3(id = "CO010", title = "West 01", region2 = getRegion2("CO01", region1)),
            NutsRegion3(id = "CO021", title = "North 01", region2 = getRegion2("CO02", region1)),
            NutsRegion3(id = "CO0E1", title = "East 01", region2 = getRegion2("CO0E", region1)),
            NutsRegion3(id = "CO0E2", title = "East 02", region2 = getRegion2("CO0E", region1)),
            NutsRegion3(id = "CO0E3", title = "East 03", region2 = getRegion2("CO0E", region1))
        )

        val audit1 = slot<AuditCandidate>()
        val audit2 = slot<AuditCandidate>()
        verifyOrder {
            auditService.logEvent(capture(audit1))
            auditService.logEvent(capture(audit2))
        }
        assertThat(audit1.captured.action).isEqualTo(AuditAction.NUTS_DATASET_DOWNLOAD)
        assertThat(audit2.captured.action).isEqualTo(AuditAction.NUTS_DATASET_DOWNLOAD)
        assertThat(audit2.captured.description).isEqualTo("NUTS Dataset 'NUTS 2020' 2020-02-18 has been downloaded.")
    }

    private fun getRegion2(id: String, region1: NutsRegion1): NutsRegion2 {
        return NutsRegion2(
            id = id,
            title = "",
            region1 = region1
        )
    }

    private fun initMetadata(date: LocalDate?, title: String?) {
        every { nutsMetadataRepository.findById(any()) } returns
            Optional.of(NutsMetadata(
                nutsDate = date,
                nutsTitle = title
            ))
    }

    @Test
    fun `retrieve downloaded nuts`() {
        initMetadata(date = LocalDate.of(2020, 1, 1), title = "2020-01-01")
        every { nutsRegion3Repository.findAll() } returns regions

        assertThat(nutsService.getNuts()).isEqualTo(
            listOf(OutputNuts("CO", "CO title", listOf(
                OutputNuts("CO0", "CO0 title", listOf(
                    OutputNuts("CO0A", "CO0A title", listOf(
                        OutputNuts("CO0A0", "CO0A0 title"),
                        OutputNuts("CO0A1", "CO0A1 title")
                    )),
                    OutputNuts("CO0B", "CO0B title", listOf(
                        OutputNuts("CO0B1", "CO0B1 title")
                    ))
                ))
            )))
        )
    }

    @Test
    fun `should return true when country and nuts are valid`(){
        initMetadata(date = LocalDate.of(2020, 1, 1), title = "2020-01-01")
        every { nutsRegion3Repository.findAll() } returns regions
      assertThat(nutsService.validateAddress("CO title (CO)", "CO0A title (CO0A)", "CO0A0 title (CO0A0)")).isTrue
    }

    @TestFactory
    fun `should return false when country and nuts are not valid`() =
        listOf(
            Triple("", "CO0A title (CO0A)", ""),
            Triple("", "", "CO0A0 title (CO0A0)"),
            Triple("AAA", "", ""),
            Triple("CO title (CO)", "", "CO0A0 title (CO0A0)"),
            Triple("CO title (CO)", "CO0A title (CO0A)", "AA"),
            Triple("CO title (CO)", "AAA", ""),
            Triple("AAA", "CO0A title (CO0A)", ""),
        ).map{ input ->
                DynamicTest.dynamicTest(
                    "should return false when country and nuts are not valid county: '${input.first}', nuts: ${input.second} , nuts3: ${input.third}"
                ) {
                    initMetadata(date = LocalDate.of(2020, 1, 1), title = "2020-01-01")

                    every { nutsRegion3Repository.findAll() } returns regions
                    assertThat(nutsService.validateAddress("CO title (CO)", "CO0A title (CO0A)", "CO0A0 title (CO0A0)")).isTrue
                }
            }
}
