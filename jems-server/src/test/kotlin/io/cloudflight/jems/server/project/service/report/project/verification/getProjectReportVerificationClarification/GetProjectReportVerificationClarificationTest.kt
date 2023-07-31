package io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationClarification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.ProjectReportVerificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetProjectReportVerificationClarificationTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 1L
        const val REPORT_ID = 3L
    }

    @MockK
    lateinit var verificationPersistence: ProjectReportVerificationPersistence

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var interactor: GetProjectReportVerificationClarification

    @Test
    fun getClarifications() {
        val projectReport = mockk<ProjectReportModel> { every { status } returns ProjectReportStatus.InVerification }
        val clarifications = mockk<List<ProjectReportVerificationClarification>>()

        every { reportPersistence.getReportById(projectId = PROJECT_ID, reportId = REPORT_ID) } returns projectReport
        every { verificationPersistence.getVerificationClarifications(reportId = REPORT_ID) } returns clarifications

        assertThat(interactor.getClarifications(projectId = PROJECT_ID, reportId = REPORT_ID)).isEqualTo(clarifications)
    }

    @Test
    fun `getClarifications - StatusInvalid`() {
        val projectReport = mockk<ProjectReportModel> { every { status } returns ProjectReportStatus.Submitted }

        every { reportPersistence.getReportById(projectId = PROJECT_ID, reportId = REPORT_ID) } returns projectReport

        assertThrows<ReportVerificationStatusNotValidException> { interactor.getClarifications(PROJECT_ID, REPORT_ID) }
    }
}
