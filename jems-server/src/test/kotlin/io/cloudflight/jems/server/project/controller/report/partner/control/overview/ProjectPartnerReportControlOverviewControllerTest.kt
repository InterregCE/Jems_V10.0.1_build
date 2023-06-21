package io.cloudflight.jems.server.project.controller.report.partner.control.overview

import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlDeductionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlDeductionOverviewRowDTO
import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlOverviewDTO
import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlWorkOverviewDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverviewRow
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview.GetReportControlDeductionOverviewInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlOverview.GetReportControlOverviewInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.GetReportControlWorkOverviewInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.overview.updateReportControlOverview.UpdateReportControlOverviewInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class ProjectPartnerReportControlOverviewControllerTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 426L
        private const val REPORT_ID = 506L

        private val dummyWorkOverview = ControlWorkOverview(
            declaredByPartner = BigDecimal.valueOf(1L),
            declaredByPartnerFlatRateSum = BigDecimal.ZERO,
            inControlSample = BigDecimal.valueOf(2L),
            inControlSamplePercentage = BigDecimal.valueOf(3L),
            parked = BigDecimal.valueOf(4L),
            deductedByControl = BigDecimal.valueOf(5L),
            eligibleAfterControl = BigDecimal.valueOf(6L),
            eligibleAfterControlPercentage = BigDecimal.valueOf(7L),
        )

        private val expectedWorkOverview = ControlWorkOverviewDTO(
            declaredByPartner = BigDecimal.valueOf(1L),
            declaredByPartnerFlatRateSum = BigDecimal.ZERO,
            inControlSample = BigDecimal.valueOf(2L),
            inControlSamplePercentage = BigDecimal.valueOf(3L),
            parked = BigDecimal.valueOf(4L),
            deductedByControl = BigDecimal.valueOf(5L),
            eligibleAfterControl = BigDecimal.valueOf(6L),
            eligibleAfterControlPercentage = BigDecimal.valueOf(7L),
        )

        private val dummyDeductionOverviewRow = ControlDeductionOverviewRow(
            typologyOfErrorId = 1L,
            typologyOfErrorName = "Typology of error 1",
            staffCost = BigDecimal.valueOf(1000),
            officeAndAdministration = BigDecimal.valueOf(1000),
            travelAndAccommodation = BigDecimal.valueOf(1000),
            externalExpertise = BigDecimal.valueOf(1000),
            equipment = BigDecimal.valueOf(1000),
            infrastructureAndWorks = BigDecimal.valueOf(1000),
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(6000)
        )

        private val dummyDeductionOverviewTotalRow = ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = BigDecimal.valueOf(1000),
            officeAndAdministration = BigDecimal.valueOf(1000),
            travelAndAccommodation = BigDecimal.valueOf(1000),
            externalExpertise = BigDecimal.valueOf(1000),
            equipment = BigDecimal.valueOf(1000),
            infrastructureAndWorks = BigDecimal.valueOf(1000),
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(6000)
        )

        private val dummyDeductionOverview = ControlDeductionOverview(
            deductionRows = mutableListOf(dummyDeductionOverviewRow),
            staffCostsFlatRate = null,
            officeAndAdministrationFlatRate = null,
            travelAndAccommodationFlatRate = null,
            otherCostsOnStaffCostsFlatRate = null,
            total = dummyDeductionOverviewTotalRow
        )

        private val expectedDeductionOverviewRow = ControlDeductionOverviewRowDTO(
            typologyOfErrorId = 1L,
            typologyOfErrorName = "Typology of error 1",
            staffCost = BigDecimal.valueOf(1000),
            officeAndAdministration = BigDecimal.valueOf(1000),
            travelAndAccommodation = BigDecimal.valueOf(1000),
            externalExpertise = BigDecimal.valueOf(1000),
            equipment = BigDecimal.valueOf(1000),
            infrastructureAndWorks = BigDecimal.valueOf(1000),
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(6000)
        )

        private val expectedDeductionOverviewTotalRow = ControlDeductionOverviewRowDTO(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = BigDecimal.valueOf(1000),
            officeAndAdministration = BigDecimal.valueOf(1000),
            travelAndAccommodation = BigDecimal.valueOf(1000),
            externalExpertise = BigDecimal.valueOf(1000),
            equipment = BigDecimal.valueOf(1000),
            infrastructureAndWorks = BigDecimal.valueOf(1000),
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(6000)
        )

        private val expectedDeductionOverview = ControlDeductionOverviewDTO(
            deductionRows = mutableListOf(expectedDeductionOverviewRow),
            staffCostsFlatRate = null,
            officeAndAdministrationFlatRate = null,
            travelAndAccommodationFlatRate = null,
            otherCostsOnStaffCostsFlatRate = null,
            total = expectedDeductionOverviewTotalRow
        )

        private val dummyOverview = ControlOverview(
            startDate = LocalDate.now(),
            requestsForClarifications = "test",
            receiptOfSatisfactoryAnswers = "test answers",
            endDate = null,
            findingDescription = null,
            followUpMeasuresFromLastReport = "test from last report",
            conclusion = "result",
            followUpMeasuresForNextReport = null,
            previousFollowUpMeasuresFromLastReport = null
        )

        private val dummyOverviewDTO = ControlOverviewDTO(
            startDate = LocalDate.now(),
            requestsForClarifications = "test",
            receiptOfSatisfactoryAnswers = "test answers",
            endDate = null,
            findingDescription = null,
            followUpMeasuresFromLastReport = "test from last report",
            conclusion = "result",
            followUpMeasuresForNextReport = null,
            previousFollowUpMeasuresFromLastReport = null,
            changedLastCertifiedReportEndDate = null,
            lastCertifiedReportNumber = null
        )

    }

    @MockK
    lateinit var getReportControlWorkOverview: GetReportControlWorkOverviewInteractor

    @MockK
    lateinit var getReportControlOverview: GetReportControlOverviewInteractor

    @MockK
    lateinit var updateReportControlOverview: UpdateReportControlOverviewInteractor

    @MockK
    lateinit var getReportControlDeductionOverview: GetReportControlDeductionOverviewInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportControlOverviewController

    @Test
    fun getControlWorkOverview() {
        every { getReportControlWorkOverview.get(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns dummyWorkOverview
        assertThat(controller.getControlWorkOverview(partnerId = PARTNER_ID, reportId = REPORT_ID)).isEqualTo(expectedWorkOverview)
    }

    @Test
    fun getControlDeductionOverview() {
        every {
            getReportControlDeductionOverview.get(
                partnerId = PARTNER_ID,
                reportId = REPORT_ID,
            )
        } returns dummyDeductionOverview
        assertThat(
            controller.getControlDeductionByTypologyOfErrorsOverview(
                partnerId = PARTNER_ID,
                reportId = REPORT_ID,
                "1.0"
            )
        ).isEqualTo(expectedDeductionOverview)
    }

    @Test
    fun getControlOverview() {
        every { getReportControlOverview.get(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns dummyOverview
        assertThat(controller.getControlOverview(partnerId = PARTNER_ID, reportId = REPORT_ID)).isEqualTo(dummyOverviewDTO)
    }

    @Test
    fun updateReportControlOverview() {
        every { updateReportControlOverview.update(partnerId = PARTNER_ID, reportId = REPORT_ID, dummyOverview) } returns dummyOverview
        assertThat(controller.updateControlOverview(partnerId = PARTNER_ID, reportId = REPORT_ID, dummyOverviewDTO)).isEqualTo(dummyOverviewDTO)
    }
}
