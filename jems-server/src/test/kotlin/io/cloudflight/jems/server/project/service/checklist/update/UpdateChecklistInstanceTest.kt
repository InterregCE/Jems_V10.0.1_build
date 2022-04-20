package io.cloudflight.jems.server.project.service.checklist.update

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateChecklistInstanceStatusNotAllowedException
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class UpdateChecklistInstanceTest : UnitTest() {

    private val CHECKLIST_ID = 100L
    private val RELATED_TO_ID = 2L
    private val PROGRAMME_CHECKLIST_ID = 4L

    private val checkLisDetail = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        relatedToId = RELATED_TO_ID,
        finishedDate = null,
        components = mutableListOf(
            ChecklistComponentInstance(
                2L,
                ProgrammeChecklistComponentType.HEADLINE,
                1,
                HeadlineMetadata("headline"),
                HeadlineInstanceMetadata()
            ),
            ChecklistComponentInstance(
                3L,
                ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                2,
                OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe"),
                OptionsToggleInstanceMetadata("yes")
            )
        )
    )

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var updateChecklistInstance: UpdateChecklistInstance

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
    }

    @Test
    fun `update - successfully`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { persistence.update(checkLisDetail) } returns checkLisDetail
        every { persistence.getStatus(checkLisDetail.id) } returns ChecklistInstanceStatus.DRAFT
        Assertions.assertThat(updateChecklistInstance.update(checkLisDetail))
            .isEqualTo(checkLisDetail)
    }

    @Test
    fun `update checklist with different status should trigger an audit log`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.update(checkLisDetail) } returns checkLisDetail
        every { persistence.getStatus(checkLisDetail.id) } returns ChecklistInstanceStatus.DRAFT

        updateChecklistInstance.update(checkLisDetail)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.ASSESSMENT_CHECKLIST_STATUS_CHANGE,
                project = AuditProject(id = checkLisDetail.relatedToId.toString()),
                description = "[" + checkLisDetail.id + "] [" + checkLisDetail.type + "]" +
                    " [" + checkLisDetail.name + "]" + " status changed from " + "[" + "DRAFT" + "]"
                    + " to " + "[" + checkLisDetail.status + "]"
            )
        )
    }

    @Test
    fun `update - checkLisDetail is already in FINISHED status`() {
        every { persistence.update(checkLisDetail) } returns checkLisDetail
        every { persistence.getStatus(checkLisDetail.id) } returns ChecklistInstanceStatus.FINISHED
        assertThrows<UpdateChecklistInstanceStatusNotAllowedException> { updateChecklistInstance.update(checkLisDetail) }
    }

}
