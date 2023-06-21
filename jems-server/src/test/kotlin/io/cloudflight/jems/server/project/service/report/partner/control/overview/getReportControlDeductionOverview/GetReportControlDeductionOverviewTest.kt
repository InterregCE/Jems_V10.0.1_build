package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverviewRow
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetReportControlDeductionOverviewTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 592L
        private const val REPORT_ID = 1L

        private const val TYPOLOGY_ID = 600L
        private const val TYPOLOGY_DESCRIPTION = "Typology of error"
    }

    @MockK
    lateinit var getReportControlDeductionOverviewService: GetReportControlDeductionOverviewService

    @InjectMockKs
    lateinit var getReportControlDeductionOverview: GetReportControlDeductionOverview

    @Test
    fun `get Overview for deductions`() {
        val deductionRows = mutableListOf(
            ControlDeductionOverviewRow(
                typologyOfErrorId = TYPOLOGY_ID,
                typologyOfErrorName = TYPOLOGY_DESCRIPTION,
                staffCost = BigDecimal.valueOf(500L),
                officeAndAdministration = BigDecimal.ZERO,
                travelAndAccommodation = BigDecimal.ZERO,
                externalExpertise = BigDecimal.ZERO,
                equipment = BigDecimal.valueOf(200L),
                infrastructureAndWorks = BigDecimal.ZERO,
                lumpSums = BigDecimal.ZERO,
                unitCosts = BigDecimal.ZERO,
                otherCosts = BigDecimal.ZERO,
                total = BigDecimal.valueOf(700L)
            ))

        val total = ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = BigDecimal.valueOf(850L),
            officeAndAdministration = BigDecimal.valueOf(12750, 2),
            travelAndAccommodation = BigDecimal.valueOf(12750, 2),
            externalExpertise = BigDecimal.ZERO,
            equipment =  BigDecimal.valueOf(200L),
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(130500, 2)
        )
      every { getReportControlDeductionOverviewService.get(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns ControlDeductionOverview(
          deductionRows = deductionRows,
          staffCostsFlatRate = null,
          officeAndAdministrationFlatRate = 15,
          travelAndAccommodationFlatRate = 15,
          otherCostsOnStaffCostsFlatRate = null,
          total = total
      )

        assertThat(getReportControlDeductionOverview.get(PARTNER_ID, REPORT_ID)).isEqualTo(
            ControlDeductionOverview(
                deductionRows = deductionRows,
                staffCostsFlatRate = null,
                officeAndAdministrationFlatRate = 15,
                travelAndAccommodationFlatRate = 15,
                otherCostsOnStaffCostsFlatRate = null,
                total = total
            )
        )

    }
}
