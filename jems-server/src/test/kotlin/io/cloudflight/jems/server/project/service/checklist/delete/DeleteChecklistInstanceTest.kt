package io.cloudflight.jems.server.project.service.checklist.delete

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteChecklistInstanceStatusNotAllowedException
import io.cloudflight.jems.server.programme.service.checklist.getList.GetChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class DeleteChecklistInstanceTest : UnitTest() {

    private val CHECKLIST_ID = 100L

    private val checkLisDetail = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        relatedToId = 1L,
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

    private val checkLisDetailWithFinishStatus = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        relatedToId = 1L,
        finishedDate = null,
        components = emptyList()
    )

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var deleteChecklistInstance: DeleteChecklistInstance

    @Test
    fun `delete checklist - OK`() {
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns checkLisDetail
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { persistence.deleteById(CHECKLIST_ID) } answers {}
        deleteChecklistInstance.deleteById(CHECKLIST_ID)
        verify { persistence.deleteById(CHECKLIST_ID) }
    }

    @Test
    fun `delete checklist - not existing`() {
        every { persistence.getChecklistDetail(-1) } throws GetChecklistInstanceDetailNotFoundException()
        assertThrows<GetChecklistInstanceDetailNotFoundException> {
            deleteChecklistInstance.deleteById(-1L)
        }
    }

    @Test
    fun `delete checklist - is already in FINISHED status`() {
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns checkLisDetailWithFinishStatus
        assertThrows<DeleteChecklistInstanceStatusNotAllowedException> { deleteChecklistInstance.deleteById(CHECKLIST_ID) }
    }
}
