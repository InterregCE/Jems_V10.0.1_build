package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.getResultPrinciple

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetProjectReportResultPrincipleTest: UnitTest() {

    @MockK
    private lateinit var projectReportResultPrinciplePersistence: ProjectReportResultPrinciplePersistence

    @InjectMockKs
    private lateinit var interactor: GetProjectReportResultPrinciple

    @BeforeEach
    fun reset() {
        clearMocks(projectReportResultPrinciplePersistence)
    }

    @Test
    fun get() {
        val projectId = 13L
        val reportId = 17L
        val resultPrinciple = mockk<ProjectReportResultPrinciple>()

        every { projectReportResultPrinciplePersistence.getProjectResultPrinciples(projectId, reportId) } returns resultPrinciple

        Assertions.assertThat(interactor.get(projectId, reportId)).isEqualTo(resultPrinciple)
    }
}
