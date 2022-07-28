package io.cloudflight.jems.server.project.repository.report.procurement

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementChange
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementSummary
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectReportProcurementPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 223L

        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)

        private fun dummyEntity(reportEntity: ProjectPartnerReportEntity) = ProjectPartnerReportProcurementEntity(
            id = 14L,
            reportEntity = reportEntity,
            contractName = "contractName",
            referenceNumber = "referenceNumber",
            contractDate = NEXT_WEEK,
            contractType = "contractType",
            contractAmount = BigDecimal.TEN,
            currencyCode = "GBP",
            supplierName = "supplierName",
            vatNumber = "vatNumber",
            comment = "comment",
            lastChanged = YESTERDAY,
        )

        private fun expectedProcurement(reportId: Long, reportNumber: Int) = ProjectPartnerReportProcurementSummary(
            id = 14L,
            reportId = reportId,
            reportNumber = reportNumber,
            createdInThisReport = false /* default */,
            lastChanged = YESTERDAY,
            contractName = "contractName",
            referenceNumber = "referenceNumber",
            contractDate = NEXT_WEEK,
            contractType = "contractType",
            contractAmount = BigDecimal.TEN,
            currencyCode = "GBP",
            supplierName = "supplierName",
            vatNumber = "vatNumber",
        )

        private fun expectedProcurementDetail(reportId: Long, reportNumber: Int) = ProjectPartnerReportProcurement(
            id = 14L,
            reportId = reportId,
            reportNumber = reportNumber,
            createdInThisReport = false /* default */,
            lastChanged = YESTERDAY,
            contractName = "contractName",
            referenceNumber = "referenceNumber",
            contractDate = NEXT_WEEK,
            contractType = "contractType",
            contractAmount = BigDecimal.TEN,
            currencyCode = "GBP",
            supplierName = "supplierName",
            vatNumber = "vatNumber",
            comment = "comment",
        )

        private fun procurementBeforeChange(id: Long, report: ProjectPartnerReportEntity) = ProjectPartnerReportProcurementEntity(
            id = id,
            reportEntity = report,
            contractName = "contractName",
            referenceNumber = "referenceNumber",
            contractDate = NEXT_WEEK,
            contractType = "contractType",
            contractAmount = BigDecimal.TEN,
            currencyCode = "GBP",
            supplierName = "supplierName",
            vatNumber = "vatNumber",
            lastChanged = YESTERDAY,
            comment = "comment",
        )

        private fun newData(id: Long) = ProjectPartnerReportProcurementChange(
            id = id,
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = NEXT_WEEK.plusDays(1),
            contractType = "contractType NEW",
            contractAmount = BigDecimal.ONE,
            currencyCode = "PLN",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
            comment = "comment NEW",
        )

        private fun expectedNewData(id: Long) = ProjectPartnerReportProcurement(
            id = id,
            reportId = 30L,
            reportNumber = 7,
            createdInThisReport = false,
            lastChanged = ZonedDateTime.now(),
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = NEXT_WEEK.plusDays(1),
            contractType = "contractType NEW",
            contractAmount = BigDecimal.ONE,
            currencyCode = "PLN",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
            comment = "comment NEW",
        )

    }

    @MockK
    lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var reportProcurementRepository: ProjectPartnerReportProcurementRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportProcurementPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportRepository)
        clearMocks(reportProcurementRepository)
    }

    @Test
    fun getById() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 74L
        every { report.number } returns 3

        every { reportProcurementRepository.findByReportEntityPartnerIdAndId(PARTNER_ID, id = 26L) } returns dummyEntity(report)

        assertThat(persistence.getById(PARTNER_ID, procurementId = 26L))
            .isEqualTo(expectedProcurementDetail(reportId = 74L, reportNumber = 3))
    }

    @Test
    fun getProcurementsForReportIds() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 75L
        every { report.number } returns 1

        every { reportProcurementRepository.findByReportEntityIdIn(setOf(5L, 6L, 7L), Pageable.unpaged()) } returns
            PageImpl(listOf(dummyEntity(report)))

        assertThat(persistence.getProcurementsForReportIds(setOf(5L, 6L, 7L), Pageable.unpaged()).content)
            .containsExactly(expectedProcurement(reportId = 75L, reportNumber = 1))
    }

    @Test
    fun getProcurementContractNamesForReportIds() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns  77L
        every { report.number } returns 1

        every { reportProcurementRepository.findTop50ByReportEntityIdIn(setOf(21L, 22L)) } returns
            listOf(dummyEntity(report))

        assertThat(persistence.getProcurementContractNamesForReportIds(setOf(21L, 22L)))
            .containsExactly(Pair(14L, "contractName"))
    }

    @Test
    fun countProcurementsForPartner() {
        every { reportProcurementRepository.countByReportEntityPartnerId(PARTNER_ID) } returns 650L
        assertThat(persistence.countProcurementsForPartner(PARTNER_ID)).isEqualTo(650L)
    }

    @Test
    fun updatePartnerReportProcurement() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 30L
        every { report.number } returns 7
        every { reportProcurementRepository.findByReportEntityPartnerIdAndReportEntityIdAndId(
            partnerId = PARTNER_ID,
            reportId = 30L,
            id = 18L,
        ) } returns procurementBeforeChange(18L, report = report)

        assertThat(
            persistence
                .updatePartnerReportProcurement(partnerId = PARTNER_ID, reportId = report.id, newData(18L))
                .copy(lastChanged = YESTERDAY)
        ).isEqualTo(expectedNewData(18L).copy(lastChanged = YESTERDAY))
    }

    @Test
    fun createPartnerReportProcurement() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { report.id } returns 30L
        every { report.number } returns 7
        every { reportRepository.findByIdAndPartnerId(30L, PARTNER_ID) } returns report

        every { reportProcurementRepository.save(any()) } returnsArgument 0

        assertThat(
            persistence
                .createPartnerReportProcurement(partnerId = PARTNER_ID, reportId = report.id, newData(0L))
                .copy(lastChanged = YESTERDAY)
        ).isEqualTo(expectedNewData(0L).copy(lastChanged = YESTERDAY))
    }

    @Test
    fun deletePartnerReportProcurement() {
        every { reportProcurementRepository
            .deleteByReportEntityPartnerIdAndReportEntityIdAndId(partnerId = PARTNER_ID, reportId = 82L, 44795L)
        } answers { }
        persistence.deletePartnerReportProcurement(partnerId = PARTNER_ID, reportId = 82L, 44795L)
        verify(exactly = 1) { reportProcurementRepository
            .deleteByReportEntityPartnerIdAndReportEntityIdAndId(partnerId = PARTNER_ID, reportId = 82L, 44795L)
        }
    }

}
