package io.cloudflight.jems.server.project.controller.report.partner.control.overview

import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlWorkOverviewDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.GetReportControlWorkOverviewInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectPartnerReportControlOverviewControllerTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 426L
        private const val REPORT_ID = 506L

        private val dummyWorkOverview = ControlWorkOverview(
            declaredByPartner = BigDecimal.valueOf(1L),
            inControlSample = BigDecimal.valueOf(2L),
            parked = BigDecimal.valueOf(3L),
            deductedByControl = BigDecimal.valueOf(4L),
            eligibleAfterControl = BigDecimal.valueOf(5L),
            eligibleAfterControlPercentage = BigDecimal.valueOf(6L),
        )

        private val expectedWorkOverview = ControlWorkOverviewDTO(
            declaredByPartner = BigDecimal.valueOf(1L),
            inControlSample = BigDecimal.valueOf(2L),
            parked = BigDecimal.valueOf(3L),
            deductedByControl = BigDecimal.valueOf(4L),
            eligibleAfterControl = BigDecimal.valueOf(5L),
            eligibleAfterControlPercentage = BigDecimal.valueOf(6L),
        )

    }

    @MockK
    lateinit var getReportControlWorkOverview: GetReportControlWorkOverviewInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportControlOverviewController

    @Test
    fun getControlWorkOverview() {
        every { getReportControlWorkOverview.get(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns dummyWorkOverview
        assertThat(controller.getControlWorkOverview(partnerId = PARTNER_ID, reportId = REPORT_ID)).isEqualTo(expectedWorkOverview)
    }

}
