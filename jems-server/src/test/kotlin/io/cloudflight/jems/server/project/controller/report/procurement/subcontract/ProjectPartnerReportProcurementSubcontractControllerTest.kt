package io.cloudflight.jems.server.project.controller.report.procurement.subcontract

import io.cloudflight.jems.api.project.dto.report.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontractDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChange
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.getProjectPartnerReportProcurementSubcontract.GetProjectPartnerReportProcurementSubcontractInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.updateProjectPartnerReportProcurementSubcontract.UpdateProjectPartnerReportProcurementSubcontractInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class ProjectPartnerReportProcurementSubcontractControllerTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 822L
        private val YEARS_AGO_10 = LocalDate.now().minusYears(10)

        private fun dummySubcontract(reportId: Long) = ProjectPartnerReportProcurementSubcontract(
            id = 275,
            reportId = reportId,
            createdInThisReport = false,
            contractName = "firstName 275",
            referenceNumber = "referenceNumber 275",
            contractDate = YEARS_AGO_10,
            contractAmount = BigDecimal.ONE,
            currencyCode = "PLN",
            supplierName = "supplierName 275",
            vatNumber = "vatNumber 275",
        )

        private fun expectedSubcontract(reportId: Long) = ProjectPartnerReportProcurementSubcontractDTO(
            id = 275,
            reportId = reportId,
            createdInThisReport = false,
            contractName = "firstName 275",
            referenceNumber = "referenceNumber 275",
            contractDate = YEARS_AGO_10,
            contractAmount = BigDecimal.ONE,
            currencyCode = "PLN",
            supplierName = "supplierName 275",
            vatNumber = "vatNumber 275",
        )

        private val dummyUpdateSubcontract = ProjectPartnerReportProcurementSubcontractChangeDTO(
            id = 275,
            contractName = "firstName 275 NEW",
            referenceNumber = "referenceNumber 275 NEW",
            contractDate = YEARS_AGO_10.minusDays(1),
            contractAmount = BigDecimal.TEN,
            currencyCode = "CZK",
            supplierName = "supplierName 275 NEW",
            vatNumber = "vatNumber 275 NEW",
        )

        private val dummyUpdateSubcontractModel = ProjectPartnerReportProcurementSubcontractChange(
            id = 275,
            contractName = "firstName 275 NEW",
            referenceNumber = "referenceNumber 275 NEW",
            contractDate = YEARS_AGO_10.minusDays(1),
            contractAmount = BigDecimal.TEN,
            currencyCode = "CZK",
            supplierName = "supplierName 275 NEW",
            vatNumber = "vatNumber 275 NEW",
        )

    }

    @MockK
    lateinit var getSubcontract: GetProjectPartnerReportProcurementSubcontractInteractor
    @MockK
    lateinit var updateSubcontract: UpdateProjectPartnerReportProcurementSubcontractInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportProcurementSubcontractController

    @Test
    fun getSubcontractors() {
        every { getSubcontract.getSubcontract(PARTNER_ID, reportId = 16L, procurementId = 82L) } returns
            listOf(dummySubcontract(reportId = 16L))
        assertThat(controller.getSubcontractors(partnerId = PARTNER_ID, reportId = 16L, procurementId = 82L))
            .containsExactly(expectedSubcontract(reportId = 16L))
    }

    @Test
    fun updateSubcontractors() {
        val subcontractListSlot = slot<List<ProjectPartnerReportProcurementSubcontractChange>>()
        every { updateSubcontract.update(PARTNER_ID, reportId = 18L, procurementId = 42L, capture(subcontractListSlot)) } returns
            listOf(dummySubcontract(reportId = 18L))

        assertThat(controller.updateSubcontractors(PARTNER_ID, reportId = 18L, procurementId = 42L, listOf(dummyUpdateSubcontract)))
            .containsExactly(expectedSubcontract(reportId = 18L))

        assertThat(subcontractListSlot.captured).containsExactly(dummyUpdateSubcontractModel)
    }

}
