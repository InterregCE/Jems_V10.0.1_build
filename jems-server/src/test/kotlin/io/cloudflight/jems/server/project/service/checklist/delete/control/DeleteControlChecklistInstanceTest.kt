package io.cloudflight.jems.server.project.service.checklist.delete.control

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getInstances.control.GetControlChecklistDetailNotAllowedException
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
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

internal class DeleteControlChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val creatorId = 1L
    private val partnerId = 2L
    private val reportId = 3L

    private val controlCheckLisDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
        relatedToId = reportId,
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

    private val controlCheckLisDetailWithFinishStatus = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
        relatedToId = reportId,
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
    lateinit var deleteControlChecklistInstance: DeleteControlChecklistInstance

    @Test
    fun `delete control checklist - OK`() {
        every { userAuthorization.getUser() } returns user
        every { securityService.getUserIdOrThrow() } returns user.id
        every { persistence.getChecklistDetail(checklistId) } returns controlCheckLisDetail
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.deleteById(checklistId) } answers {}
        deleteControlChecklistInstance.deleteById(partnerId, reportId, checklistId)
        verify { persistence.deleteById(checklistId) }

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CONTROL_CHECKLIST_DELETED,
                project = AuditProject(id = controlCheckLisDetail.relatedToId.toString()),
                description = "Checklist [${controlCheckLisDetail.id}] type [${controlCheckLisDetail.type}] name [${controlCheckLisDetail.name}] " +
                        "for [${partnerId}] and [${reportId}] was deleted by [${user.id}]"
            )
        )
    }

    @Test
    fun `delete control checklist - does not exist`() {
        every { persistence.getChecklistDetail(-1) } throws GetControlChecklistDetailNotAllowedException()
        assertThrows<GetControlChecklistDetailNotAllowedException> {
            deleteControlChecklistInstance.deleteById(partnerId, reportId, -1L)
        }
    }

    @Test
    fun `delete control checklist - is already in FINISHED status (cannot be deleted)`() {
        every { persistence.getChecklistDetail(checklistId) } returns controlCheckLisDetailWithFinishStatus
        assertThrows<DeleteControlChecklistInstanceStatusNotAllowedException> { deleteControlChecklistInstance.deleteById(partnerId, reportId, checklistId) }
    }
}