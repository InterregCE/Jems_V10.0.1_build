package io.cloudflight.jems.server.project.service.report.project.base.deleteProjectReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher

internal class DeleteProjectReportTest : UnitTest() {

    companion object {
        private fun currentLatestReport(
            reportId: Long,
            projectId: Long,
            number: Int,
            status: ProjectReportStatus,
        ): ProjectReportModel {
            val mock = mockk<ProjectReportModel>()
            every { mock.id } returns reportId
            every { mock.reportNumber } returns number
            every { mock.status } returns status
            every { mock.projectId } returns projectId
            every { mock.projectIdentifier } returns "proj-iden"
            every { mock.projectAcronym } returns "proj-acronym"
            return mock
        }
    }

    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: DeleteProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, auditPublisher)
    }

    @ParameterizedTest(name = "delete {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft"])
    fun delete(status: ProjectReportStatus) {
        val projectId = 61L + status.ordinal
        every { reportPersistence.getCurrentLatestReportFor(projectId) } returns
            currentLatestReport(22L, projectId, 4, status)
        every { reportPersistence.deleteReport(projectId, any()) } answers { }

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}

        interactor.delete(projectId, reportId = 22L)
        verify(exactly = 1) { reportPersistence.deleteReport(projectId, 22L) }

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PROJECT_REPORT_DELETED,
            project = AuditProject(projectId.toString(), "proj-iden", "proj-acronym"),
            entityRelatedId = 22L,
            description = "[proj-iden] Draft project report PR.4 deleted",
        ))
    }

    @ParameterizedTest(name = "delete - report closed {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `delete - report closed`(status: ProjectReportStatus) {
        val projectId = 161L + status.ordinal
        every { reportPersistence.getCurrentLatestReportFor(projectId) } returns
            currentLatestReport(25L, projectId, 7, status)

        assertThrows<OnlyLastOpenReportCanBeDeleted> { interactor.delete(projectId, reportId = 25L) }

        verify(exactly = 0) { reportPersistence.deleteReport(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @ParameterizedTest(name = "delete - report not latest {0}")
    @EnumSource(value = ProjectReportStatus::class)
    fun `delete - report not latest`(status: ProjectReportStatus) {
        val projectId = 261L + status.ordinal
        every { reportPersistence.getCurrentLatestReportFor(projectId) } returns
            currentLatestReport(30L, projectId, 8, status)

        assertThrows<OnlyLastOpenReportCanBeDeleted> { interactor.delete(projectId, reportId = -1L) }

        verify(exactly = 0) { reportPersistence.deleteReport(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `delete - no report`() {
        every { reportPersistence.getCurrentLatestReportFor(-1L) } returns null

        assertThrows<ThereIsNoAnyReportForProject> { interactor.delete(-1L, reportId = 0L) }

        verify(exactly = 0) { reportPersistence.deleteReport(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

}
