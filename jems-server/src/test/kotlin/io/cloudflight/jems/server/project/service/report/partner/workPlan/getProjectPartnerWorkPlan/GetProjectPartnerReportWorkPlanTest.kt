package io.cloudflight.jems.server.project.service.report.partner.workPlan.getProjectPartnerWorkPlan

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetProjectPartnerReportWorkPlanTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 520L
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var getReportWorkPlan: GetProjectPartnerReportWorkPlan

    @Test
    fun getForPartner() {
        val workPlan = mockk<ProjectPartnerReportWorkPackage>()
        every { reportPersistence.getPartnerReportWorkPlanById(PARTNER_ID, reportId = 97L) } returns listOf(workPlan)
        assertThat(getReportWorkPlan.getForPartner(PARTNER_ID, reportId = 97L)).containsExactly(workPlan)
    }
}
