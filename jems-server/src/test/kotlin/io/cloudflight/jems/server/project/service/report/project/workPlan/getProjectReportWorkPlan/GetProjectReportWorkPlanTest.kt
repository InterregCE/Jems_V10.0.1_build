package io.cloudflight.jems.server.project.service.report.project.workPlan.getProjectReportWorkPlan

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetProjectReportWorkPlanTest: UnitTest() {

    @MockK
    private lateinit var reportWorkPlanPersistence: ProjectReportWorkPlanPersistence

    @InjectMockKs
    private lateinit var interactor: GetProjectReportWorkPlan

    @BeforeEach
    fun reset() {
        clearMocks(reportWorkPlanPersistence)
    }

    @Test
    fun get() {
        val workPlan = mockk<List<ProjectReportWorkPackage>>()
        every { reportWorkPlanPersistence.getReportWorkPlanById(projectId = 10L, reportId = 20L) } returns workPlan
        assertThat(interactor.get(10L, reportId = 20L)).isEqualTo(workPlan)
    }
}
