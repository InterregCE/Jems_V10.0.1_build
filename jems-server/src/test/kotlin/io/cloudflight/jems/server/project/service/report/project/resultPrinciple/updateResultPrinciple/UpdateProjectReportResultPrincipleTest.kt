package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.updateResultPrinciple

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrincipleUpdate
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateProjectReportResultPrincipleTest : UnitTest() {

    @MockK
    private lateinit var projectReportResultPrinciplePersistence: ProjectReportResultPrinciplePersistence

    @MockK
    private lateinit var projectReportPersistence: ProjectReportPersistence

    @InjectMockKs
    private lateinit var interactor: UpdateProjectReportResultPrinciple

    @BeforeEach
    fun reset() {
        clearMocks(projectReportResultPrinciplePersistence, projectReportPersistence)
    }

    @Test
    fun update() {
        val projectId = 15L
        val reportId = 17L
        val resultPrincipleUpdate = mockk<ProjectReportResultPrincipleUpdate>()
        val resultPrinciple = mockk<ProjectReportResultPrinciple>()
        val projectReport = mockk<ProjectReportModel> {
            every { status } returns ProjectReportStatus.Draft
        }

        every { projectReportPersistence.getReportById(projectId, reportId) } returns projectReport
        every { projectReportResultPrinciplePersistence.updateProjectReportResultPrinciple(projectId, reportId, resultPrincipleUpdate) } returns resultPrinciple

        Assertions.assertThat(interactor.update(projectId, reportId, resultPrincipleUpdate)).isEqualTo(resultPrinciple)
    }
}
