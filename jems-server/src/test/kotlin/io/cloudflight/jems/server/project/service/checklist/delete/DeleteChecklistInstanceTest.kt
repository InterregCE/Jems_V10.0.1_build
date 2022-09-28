package io.cloudflight.jems.server.project.service.checklist.delete

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.delete.DeleteChecklistInstanceStatusNotAllowedException
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.*
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.utils.user
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal

internal class DeleteChecklistInstanceTest : UnitTest() {

    private val CHECKLIST_ID = 100L
    private val CREATOR_ID = 1L

    private val checkLisDetail = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "a@a",
        creatorId = CREATOR_ID,
        relatedToId = 1L,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = false,
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
                OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe", ""),
                OptionsToggleInstanceMetadata("yes","test")
            ),
            ChecklistComponentInstance(
                4L,
                ProgrammeChecklistComponentType.TEXT_INPUT,
                3,
                TextInputMetadata("Question to be answered", "Label", 2000),
                TextInputInstanceMetadata("Explanation")
            )
        )
    )

    private val checkLisDetailWithFinishStatus = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "a@a",
        creatorId = CREATOR_ID,
        relatedToId = 1L,
        finishedDate = null,
        consolidated = false,
        visible = true,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        components = emptyList()
    )

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var deleteChecklistInstance: DeleteChecklistInstance

    @Test
    fun `delete checklist - OK`() {
        every { userAuthorization.getUser() } returns user
        every { securityService.getUserIdOrThrow() } returns user.id
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns checkLisDetail
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.deleteById(CHECKLIST_ID) } answers {}
        deleteChecklistInstance.deleteById(CHECKLIST_ID)
        verify { persistence.deleteById(CHECKLIST_ID) }

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.ASSESSMENT_CHECKLIST_DELETED,
                project = AuditProject(id = checkLisDetail.relatedToId.toString()),
                description = "[${checkLisDetail.id}] [${checkLisDetail.type}] [${checkLisDetail.name}] deleted"
            )
        )
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
