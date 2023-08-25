package io.cloudflight.jems.server.project.service.report.project.base.reOpenProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class ReOpenProjectReportTest : UnitTest() {

    companion object {
        private fun submissionSummary(projectId: Long, reportId: Long, newStatus: ProjectReportStatus) = ProjectReportSubmissionSummary(
            id = reportId,
            reportNumber = 17,
            status = newStatus,
            version = "V7.7",
            firstSubmission = mockk(),
            createdAt = mockk(),
            projectId = projectId,
            projectIdentifier = "proj-iden",
            projectAcronym = "proj-acr",
        )
    }

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var interactor: ReOpenProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, eventPublisher)
    }

    @ParameterizedTest(name = "reOpen from {1} to {2}")
    @CsvSource(value = [
        "41,Submitted,ReOpenSubmittedLast,41",
        "42,Submitted,ReOpenSubmittedLimited,555",
        "43,InVerification,VerificationReOpenedLast,43",
        "44,InVerification,VerificationReOpenedLimited,555",
        "45,ReOpenFinalized,VerificationReOpenedLast,45",
        "46,ReOpenFinalized,VerificationReOpenedLimited,555",
    ])
    fun reOpen(reportId: Long, statusFrom: ProjectReportStatus, expectedStatus: ProjectReportStatus, lastReportId: Long) {
        val projectId = 25L
        val report = mockk<ProjectReportModel>()
        every { report.id } returns reportId
        every { report.projectId } returns projectId
        every { report.status } returns statusFrom
        every { report.type } returns ContractingDeadlineType.Finance
        every { reportPersistence.getReportById(projectId, reportId) } returns report

        val latestReportOfType = mockk<ProjectReportModel>()
        every { latestReportOfType.id } returns lastReportId
        every { reportPersistence.getCurrentLatestReportOfType(projectId, ContractingDeadlineType.Finance) } returns latestReportOfType

        val slotTime = slot<ZonedDateTime>()
        val result = submissionSummary(projectId, reportId, expectedStatus)
        every { reportPersistence.reOpenReportTo(reportId, any(), capture(slotTime)) } returns result

        val slotAudit = slot<AuditCandidateEvent>()
        every { eventPublisher.publishEvent(capture(slotAudit)) } answers { }
        val slotStatusChanged = slot<ProjectReportStatusChanged>()
        every { eventPublisher.publishEvent(capture(slotStatusChanged)) } answers { }

        interactor.reOpen(projectId, reportId)

        verify(exactly = 1) { reportPersistence.reOpenReportTo(reportId, expectedStatus, any()) }

        assertThat(slotStatusChanged.captured.projectReportSummary).isEqualTo(result)
        assertThat(slotStatusChanged.captured.previousReportStatus).isEqualTo(statusFrom)
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(AuditAction.PROJECT_REPORT_REOPENED,
                AuditProject("25", "proj-iden", "proj-acr"), reportId, "PR.17 was reopened")
        )
    }

    @ParameterizedTest(name = "reOpen forbidden from {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Submitted", "InVerification", "ReOpenFinalized"], mode = EnumSource.Mode.EXCLUDE)
    fun reOpen(statusFrom: ProjectReportStatus) {
        val projectId = 35L
        val report = mockk<ProjectReportModel>()
        every { report.status } returns statusFrom
        every { reportPersistence.getReportById(projectId, 84L) } returns report

        assertThrows<ReportCanNotBeReOpened> { interactor.reOpen(projectId, 84L) }
    }

}
