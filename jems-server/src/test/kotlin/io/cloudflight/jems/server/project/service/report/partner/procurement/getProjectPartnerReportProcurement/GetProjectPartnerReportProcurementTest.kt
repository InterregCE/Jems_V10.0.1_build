package io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementSummary
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class GetProjectPartnerReportProcurementTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5776L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val LAST_WEEK = LocalDate.now().minusWeeks(1)

        private val procurementFrom53 = ProjectPartnerReportProcurementSummary(
            id = 100L,
            reportId = 53L,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName 100",
            referenceNumber = "referenceNumber 100",
            contractDate = LAST_WEEK,
            contractType = "contractType 100",
            contractAmount = BigDecimal.TEN,
            currencyCode = "GBP",
            supplierName = "supplierName 100",
            vatNumber = "vat number 100",
        )
        private val procurementFrom54 = ProjectPartnerReportProcurementSummary(
            id = 101L,
            reportId = 54L,
            reportNumber = 2,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName 101",
            referenceNumber = "referenceNumber 101",
            contractDate = LAST_WEEK,
            contractType = "contractType 101",
            contractAmount = BigDecimal.ONE,
            currencyCode = "CZK",
            supplierName = "supplierName 101",
            vatNumber = "vat number 101",
        )

        private val procurement = ProjectPartnerReportProcurement(
            id = 90L,
            reportId = 55L,
            reportNumber = 3,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName 102",
            referenceNumber = "referenceNumber 102",
            contractDate = LAST_WEEK,
            contractType = "contractType 102",
            contractAmount = BigDecimal.ONE,
            currencyCode = "CZK",
            supplierName = "supplierName 102",
            vatNumber = "vat number 102",
            comment = "comment 102",
        )

    }

    private fun mockExists(reportId: Long, exists: Boolean) {
        every { reportPersistence.exists(PARTNER_ID, reportId) } returns exists
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportProcurement

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        mockExists(54L, true)
        mockExists(-1L, false)
    }

    @Test
    fun getProcurement() {
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 54) } returns setOf(53L)
        every { reportProcurementPersistence.getProcurementsForReportIds(setOf(53L, 54L), any()) } returns
            PageImpl(listOf(procurementFrom54, procurementFrom53))

        assertThat(interactor.getProcurement(PARTNER_ID, reportId = 54L, Pageable.unpaged()))
            .containsExactly(
                procurementFrom54.copy(createdInThisReport = true),
                procurementFrom53.copy(createdInThisReport = false),
            )
    }

    @Test
    fun `getProcurement - not existing`() {
        assertThrows<PartnerReportNotFound> { interactor.getProcurement(PARTNER_ID, reportId = -1L, Pageable.unpaged()) }
    }

    @Test
    fun getProcurementById() {
        every { reportProcurementPersistence.getById(PARTNER_ID, procurementId = 90L) } returns procurement
        assertThat(interactor.getProcurementById(PARTNER_ID, reportId = 1L, procurementId = 90L)).isEqualTo(procurement)
    }

    @Test
    fun `getProcurementById - from current report`() {
        every { reportProcurementPersistence.getById(PARTNER_ID, procurementId = 90L) } returns procurement
        assertThat(interactor.getProcurementById(PARTNER_ID, reportId = 55L, procurementId = 90L))
            .isEqualTo(procurement.copy(createdInThisReport = true))
    }

    @Test
    fun getProcurementsForSelector() {
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 54) } returns setOf(53L)
        val captureSort = slot<Pageable>()
        every { reportProcurementPersistence.getProcurementsForReportIds(setOf(53L, 54L), capture(captureSort)) } returns
            PageImpl(listOf(procurementFrom54, procurementFrom53))

        assertThat(interactor.getProcurementsForSelector(PARTNER_ID, reportId = 54L))
            .containsExactly(
                IdNamePair(101L, "contractName 101"),
                IdNamePair(100L, "contractName 100"),
            )
        assertThat(captureSort.captured).isEqualTo(
            PageRequest.of(0, 50, Sort.by(
                Sort.Order(Sort.Direction.DESC, "reportEntity.id"),
                Sort.Order(Sort.Direction.DESC, "id"),
            ))
        )
    }

    @Test
    fun `getProcurementsForSelector - not existing`() {
        assertThrows<PartnerReportNotFoundForSelector> { interactor.getProcurementsForSelector(PARTNER_ID, reportId = -1L) }
    }

}
