package io.cloudflight.jems.server.project.service.checklist.delete.contracting

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
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.project.service.checklist.getInstances.contracting.GetContractingChecklistInstancesInteractor
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
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

internal class DeleteContractingChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val creatorId = 1L
    private val projectId = 5L

    private val contractingChecklist = ChecklistInstance(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        creatorEmail = "a@a",
        relatedToId = projectId,
        finishedDate = null,
        consolidated = false,
        visible = false,
        description = "test"
    )

    private val contractingChecklistDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
        relatedToId = projectId,
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
                OptionsToggleInstanceMetadata("yes", "test")
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

    private val contractingChecklistDetailWithFinishStatus = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
        relatedToId = projectId,
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
    lateinit var getContractingChecklistInteractor: GetContractingChecklistInstancesInteractor

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @InjectMockKs
    lateinit var deleteContractingChecklistInstance: DeleteContractingChecklistInstance

    @Test
    fun `delete contracting checklist - OK`() {
        every { userAuthorization.getUser() } returns user
        every { securityService.getUserIdOrThrow() } returns user.id
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CONTRACTING,
                projectId
            )
        } returns contractingChecklistDetail
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.deleteById(checklistId) } answers {}
        deleteContractingChecklistInstance.deleteById(projectId, checklistId)
        verify { persistence.deleteById(checklistId) }

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CHECKLIST_DELETED,
                project = AuditProject(id = projectId.toString()),
                description = "Checklist ${contractingChecklistDetail.id} " +
                        "type ${contractingChecklistDetail.type} " +
                        "name ${contractingChecklistDetail.name} " +
                        "for contract monitoring was deleted"
            )
        )
    }

    @Test
    fun `delete contracting checklist - does not exist`() {
        every {
            persistence.getChecklistDetail(
                -1,
                ProgrammeChecklistType.CONTRACTING,
                projectId
            )
        } throws GetChecklistInstanceDetailNotFoundException()
        assertThrows<GetChecklistInstanceDetailNotFoundException> {
            deleteContractingChecklistInstance.deleteById(projectId, -1L)
        }
    }

    @Test
    fun `delete contracting checklist - is already in FINISHED status (cannot be deleted)`() {
        every { getContractingChecklistInteractor.getContractingChecklistInstances(projectId) } returns listOf(
            contractingChecklist
        )
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CONTRACTING,
                projectId
            )
        } returns contractingChecklistDetailWithFinishStatus
        assertThrows<DeleteContractingChecklistInstanceStatusNotAllowedException> {
            deleteContractingChecklistInstance.deleteById(
                projectId,
                checklistId
            )
        }
    }
}
