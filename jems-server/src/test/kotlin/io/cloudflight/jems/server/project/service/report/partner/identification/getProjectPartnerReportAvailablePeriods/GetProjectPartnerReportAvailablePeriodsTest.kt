package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetProjectPartnerReportAvailablePeriodsTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 520L
    }

    @MockK
    lateinit var reportIdentificationPersistence: ProjectReportIdentificationPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectPartnerReportAvailablePeriods

    @Test
    fun get() {
        val period0 = mockk<ProjectPartnerReportPeriod>()
        every { period0.number } returns 0
        val period1 = mockk<ProjectPartnerReportPeriod>()
        every { period1.number } returns 1
        val period255 = mockk<ProjectPartnerReportPeriod>()
        every { period255.number } returns 255
        every { reportIdentificationPersistence.getAvailablePeriods(PARTNER_ID, reportId = 352L) } returns
            listOf(period0, period1, period255)

        assertThat(interactor.get(PARTNER_ID, reportId = 352L)).containsExactly(period1)
    }

}
