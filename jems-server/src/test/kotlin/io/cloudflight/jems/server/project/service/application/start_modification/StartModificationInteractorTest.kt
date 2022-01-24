package io.cloudflight.jems.server.project.service.application.start_modification

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
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
        every { projectPersistence.getProjectSummary(StartModificationInteractorTest.PROJECT_ID) } returns StartModificationInteractorTest.summary
        every { applicationStateFactory.getInstance(any()) } returns approvedState
        every { projectPersistence.getApplicantAndStatusById(any()).projectStatus } returns ApplicationStatus.APPROVED
        every { approvedState.startModification() } returns ApplicationStatus.MODIFICATION_PRECONTRACTING

        val slotAudit = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) }.returnsMany(Unit)

        Assertions.assertThat(startModification.startModification(StartModificationInteractorTest.PROJECT_ID))
            .isEqualTo(ApplicationStatus.MODIFICATION_PRECONTRACTING)

        verify(exactly = 2) { auditPublisher.publishEvent(or(slotAudit[0], slotAudit[1])) }

        Assertions.assertThat(slotAudit[0].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(
                    id = StartModificationInteractorTest.PROJECT_ID.toString(),
                    customIdentifier = "01",
                    name = "project acronym"
                ),
                description = "Project application status changed from APPROVED to MODIFICATION_PRECONTRACTING"
            )
        )

        Assertions.assertThat(slotAudit[1].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_VERSION_RECORDED,
                project = AuditProject(
                    id = StartModificationInteractorTest.PROJECT_ID.toString(),
                    customIdentifier = "01",
                    name = "project acronym"
                ),
                description = slotAudit[1].auditCandidate.description
            )
        )
    }
}
