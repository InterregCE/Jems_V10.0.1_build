package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetReportControlWorkOverviewTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 592L
    }


    @MockK
    private lateinit var getReportControlWorkOverviewService: GetReportControlWorkOverviewService

    @InjectMockKs
    private lateinit var interactor: GetReportControlWorkOverview


    @Test
    fun get() {
        every { getReportControlWorkOverviewService
            .get(PARTNER_ID, reportId = 22L)
        } returns ControlWorkOverview( declaredByPartner = BigDecimal.TEN,
            inControlSample = BigDecimal.ONE,
            parked = BigDecimal.valueOf(115, 2),
            deductedByControl = BigDecimal.valueOf(724L, 2),
            eligibleAfterControl = BigDecimal.valueOf(161L, 2),
            eligibleAfterControlPercentage = BigDecimal.valueOf(1610L, 2),
            inControlSamplePercentage = BigDecimal.valueOf(1000L, 2)
        )

        assertThat(interactor.get(PARTNER_ID, reportId = 22L)).isEqualTo(
            ControlWorkOverview(
                declaredByPartner = BigDecimal.TEN,
                inControlSample = BigDecimal.ONE,
                inControlSamplePercentage = BigDecimal.valueOf(1000L, 2),
                parked = BigDecimal.valueOf(115, 2),
                deductedByControl = BigDecimal.valueOf(724L, 2),
                eligibleAfterControl = BigDecimal.valueOf(161L, 2),
                eligibleAfterControlPercentage = BigDecimal.valueOf(1610L, 2),
            )
        )
    }

}
