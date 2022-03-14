package io.cloudflight.jems.server.project.controller.report.expenditureCosts

import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportExpenditureCostDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.PartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.partnerReportExpenditureCosts.PartnerReportExpenditureCostsInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

internal class PartnerReportExpenditureCostsControllerTest : UnitTest() {

    private val reportExpenditureCost = PartnerReportExpenditureCost(
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

    private val reportExpenditureCostDto = PartnerReportExpenditureCostDTO(
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
    lateinit var partnerReportExpenditureCostsInteractor: PartnerReportExpenditureCostsInteractor

    @InjectMockKs
    private lateinit var controller: PartnerReportExpenditureCostsController


    @Test
    fun updatePartnerReportExpenditures() {
        val partnerExpenditures = listOf(reportExpenditureCost)
        every {
            partnerReportExpenditureCostsInteractor.updatePartnerReportExpenditureCosts(
                18, 18, partnerExpenditures
            )
        } returns partnerExpenditures
        assertThat(controller.updatePartnerReportExpenditures(18, 18, listOf(reportExpenditureCostDto)))
            .containsExactly(reportExpenditureCostDto)
    }
}
