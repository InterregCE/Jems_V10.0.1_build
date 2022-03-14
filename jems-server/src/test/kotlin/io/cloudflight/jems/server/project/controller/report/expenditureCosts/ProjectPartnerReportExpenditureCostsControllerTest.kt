package io.cloudflight.jems.server.project.controller.report.expenditureCosts

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportExpenditureCostDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure.GetProjectPartnerReportExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure.UpdateProjectPartnerReportExpenditureInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

internal class ProjectPartnerReportExpenditureCostsControllerTest : UnitTest() {

    private val PARTNER_ID = 11L

    private val reportExpenditureCost = ProjectPartnerReportExpenditureCost(
        id = 754,
        costCategory = "costCategory",
        investmentNumber = "number-1",
        contractId = "",
        internalReferenceNumber = "internal-1",
        invoiceNumber = "invoice-1",
        invoiceDate = LocalDate.of(2022, 1, 1),
        dateOfPayment = LocalDate.of(2022, 2, 1),
        description = emptySet(),
        comment = emptySet(),
        totalValueInvoice = BigDecimal.valueOf(22),
        vat = BigDecimal.valueOf(18.0),
        declaredAmount = BigDecimal.valueOf(1.3)
    )

    private val reportExpenditureCostDto = ProjectPartnerReportExpenditureCostDTO(
        id = 754,
        costCategory = "costCategory",
        investmentNumber = "number-1",
        contractId = "",
        internalReferenceNumber = "internal-1",
        invoiceNumber = "invoice-1",
        invoiceDate = LocalDate.of(2022, 1, 1),
        dateOfPayment = LocalDate.of(2022, 2, 1),
        description = emptySet(),
        comment = emptySet(),
        totalValueInvoice = BigDecimal.valueOf(22),
        vat = BigDecimal.valueOf(18.0),
        declaredAmount = BigDecimal.valueOf(1.3)
    )


    @MockK
    lateinit var getProjectPartnerReportExpenditureInteractor: GetProjectPartnerReportExpenditureInteractor

    @MockK
    lateinit var updateProjectPartnerReportExpenditureInteractor: UpdateProjectPartnerReportExpenditureInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportExpenditureCostsController

    @Test
    fun getProjectPartnerReports() {
        every { getProjectPartnerReportExpenditureInteractor.getExpenditureCosts(PARTNER_ID, reportId = 17L) } returns
            listOf(reportExpenditureCost)
        assertThat(controller.getProjectPartnerReports(PARTNER_ID, reportId = 17L))
            .containsExactly(reportExpenditureCostDto)
    }

    @Test
    fun updatePartnerReportExpenditures() {
        val slotData = slot<List<ProjectPartnerReportExpenditureCost>>()

        every { updateProjectPartnerReportExpenditureInteractor.updatePartnerReportExpenditureCosts(
            partnerId = PARTNER_ID,
            reportId = 20L,
            capture(slotData),
        ) } returns listOf(reportExpenditureCost)

        assertThat(controller.updatePartnerReportExpenditures(PARTNER_ID, reportId = 20L, listOf(reportExpenditureCostDto)))
            .containsExactly(reportExpenditureCostDto)

        assertThat(slotData.captured).hasSize(1)
        assertThat(slotData.captured.first()).isEqualTo(reportExpenditureCost)
    }

}
