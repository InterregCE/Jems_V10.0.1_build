package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerControlReportParkedExpenditures

import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportParkedExpenditureIds.GetProjectPartnerControlReportParkedExpenditures
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

internal class GetProjectPartnerControlReportParkedExpendituresTest {

    @MockK
    private lateinit var reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence

    @InjectMockKs
    lateinit var getControlReportParkedExpenditures: GetProjectPartnerControlReportParkedExpenditures


    @Test
    fun getParkedExpenditureIds() {
        every {
            reportParkedExpenditurePersistence.getParkedExpenditureIds(11L)
        } returns setOf(1L, 2L, 4L, 6L, 19L)

        assertThat(getControlReportParkedExpenditures.getParkedExpenditureIds(100L, 11L))
            .isEqualTo(listOf(1L, 2L, 4L, 6L, 19L))
    }
}
