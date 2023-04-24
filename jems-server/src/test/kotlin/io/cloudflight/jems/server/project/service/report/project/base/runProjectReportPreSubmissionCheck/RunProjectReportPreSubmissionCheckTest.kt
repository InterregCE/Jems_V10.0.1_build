package io.cloudflight.jems.server.project.service.report.project.base.runProjectReportPreSubmissionCheck

import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RunProjectReportPreSubmissionCheckTest : UnitTest() {

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var service: RunProjectReportPreSubmissionCheckService

    @InjectMockKs
    private lateinit var interactor: RunProjectReportPreSubmissionCheck

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(service)
    }

    @Test
    fun preCheck() {
        every { reportPersistence.exists(projectId = 4L, reportId = 48L) } returns true
        val result = mockk<PreConditionCheckResult>()
        every { service.preCheck(projectId = 4L, reportId = 48L) } returns result

        assertThat(interactor.preCheck(projectId = 4L, reportId = 48L)).isEqualTo(result)
    }

    @Test
    fun `preCheck - not existing`() {
        every { reportPersistence.exists(projectId = 1L, reportId = -1L) } returns false
        assertThrows<ReportNotFound> { interactor.preCheck(projectId = 1L, reportId = -1L) }
        verify(exactly = 0) { service.preCheck(any(), any()) }
    }

}
