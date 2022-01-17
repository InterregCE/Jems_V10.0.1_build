package io.cloudflight.jems.server.project.service.application.reject_modification

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.ModificationPreContractingSubmittedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate

class RejectModificationInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val dateNow = LocalDate.now()
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.MODIFICATION_PRECONTRACTING
        )
        private val actionInfo = ApplicationActionInfo(
            note = "note",
            date = dateNow,
            entryIntoForceDate = dateNow
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    private lateinit var rejectModification: RejectModification

    @MockK
    lateinit var inModificationState: ModificationPreContractingSubmittedApplicationState

    @Test
    fun reject() {
        every { generalValidator.maxLength(actionInfo.note, 10000, "note") } returns emptyMap()
        every { generalValidator.notNull(dateNow, "decisionDate") } returns emptyMap()
        every { generalValidator.dateNotInFuture(dateNow, "decisionDate") } returns emptyMap()
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit

        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns inModificationState
        every { inModificationState.rejectModification(actionInfo) } returns ApplicationStatus.MODIFICATION_REJECTED

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) }.returnsMany(Unit)

        assertThat(rejectModification.reject(PROJECT_ID, actionInfo)).isEqualTo(ApplicationStatus.MODIFICATION_REJECTED)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from MODIFICATION_PRECONTRACTING to MODIFICATION_REJECTED"
            )
        )
    }

}
