package io.cloudflight.jems.server.project.service.checklist.consolidate

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.ProjectChecklistAuthorization
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.consolidateInstance.ConsolidateChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

internal class ConsolidateChecklistInstanceTest : UnitTest() {

    @RelaxedMockK
    lateinit var persistence: ChecklistInstancePersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var checklistAuthorization: ProjectChecklistAuthorization

    @InjectMockKs
    lateinit var consolidateChecklistInstance: ConsolidateChecklistInstance

    private val checklist = ChecklistInstance(
        id = 1L,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "a@a",
        relatedToId = 1L,
        finishedDate = null,
        consolidated = true,
        visible = true
    )

    @Test
    fun `consolidate checklist`() {
        every { checklistAuthorization.canConsolidate(1L) } returns true
        every { persistence.getChecklistSummary(1L) } returns checklist
        every { persistence.consolidateChecklistInstance(1L, true) } returns checklist

        consolidateChecklistInstance.consolidateChecklistInstance(1, true)

        verify { persistence.consolidateChecklistInstance(1, true) }

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action= AuditAction.ASSESSMENT_CHECKLIST_CONSOLIDATION_CHANGE,
                project= AuditProject(id="1", customIdentifier=null, name=null),
                entityRelatedId=null,
                description="[1] [APPLICATION_FORM_ASSESSMENT] [name] consolidation set to true"
            )
        )
    }
}
