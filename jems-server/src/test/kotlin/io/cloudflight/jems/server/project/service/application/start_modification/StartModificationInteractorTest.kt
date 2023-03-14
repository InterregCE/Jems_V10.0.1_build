package io.cloudflight.jems.server.project.service.application.start_modification

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.save_project_version.CreateNewProjectVersionInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class StartModificationInteractorTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 1L,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.APPROVED
        )
    }


    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var projectVersionPersistence: ProjectVersionPersistence

    @RelaxedMockK
    lateinit var projectWorkflowPersistance: ProjectWorkflowPersistence

    @RelaxedMockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var createNewProjectVersion: CreateNewProjectVersionInteractor

    @InjectMockKs
    private lateinit var startModification: StartModification

    @MockK
    lateinit var approvedState: ApprovedApplicationState


    @BeforeEach
    fun clearMocks() {
        io.mockk.clearMocks(auditPublisher)
    }

    @Test
    fun startModifications() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns StartModificationInteractorTest.summary
        every { applicationStateFactory.getInstance(any()) } returns approvedState
        every { projectPersistence.getApplicantAndStatusById(any()).projectStatus } returns ApplicationStatus.APPROVED
        every { approvedState.startModification() } returns ApplicationStatus.MODIFICATION_PRECONTRACTING


        val slotAuditStatus = slot<JemsAuditEvent>()
        val slotAuditVersion = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAuditStatus)) }.returns(Unit)
        every { auditPublisher.publishEvent(capture(slotAuditVersion)) }.returns(Unit)


        Assertions.assertThat(startModification.startModification(PROJECT_ID))
            .isEqualTo(ApplicationStatus.MODIFICATION_PRECONTRACTING)

        verify (exactly = 1){ auditPublisher.publishEvent(capture(slotAuditStatus)) }
        verify (exactly = 1){ auditPublisher.publishEvent(capture(slotAuditVersion)) }


        Assertions.assertThat(slotAuditStatus.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(
                    id = PROJECT_ID.toString(),
                    customIdentifier = "01",
                    name = "project acronym"
                ),
                description = "Project application status changed from APPROVED to MODIFICATION_PRECONTRACTING"
            )
        )

        Assertions.assertThat(slotAuditVersion.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_VERSION_RECORDED,
                project = AuditProject(
                    id = PROJECT_ID.toString(),
                    customIdentifier = "01",
                    name = "project acronym"
                ),
                description = slotAuditVersion.captured.auditCandidate.description
            )
        )
    }
}
