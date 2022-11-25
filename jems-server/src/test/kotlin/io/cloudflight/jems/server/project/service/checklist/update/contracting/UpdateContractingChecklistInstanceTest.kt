package io.cloudflight.jems.server.project.service.checklist.update.contracting

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ScoreMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstanceValidator
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.ScoreInstanceMetadata
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.utils.user
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal

internal class UpdateContractingChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val programmeChecklistId = 4L
    private val creatorId = 1L
    private val creatorEmail = "a@a"
    private val notCreatorEmail = "b@b"
    private val projectId = 7L

    private val contractingChecklistDetail = contractingChecklistInstanceDetail()

    private fun contractingChecklistInstanceDetail(status: ChecklistInstanceStatus = ChecklistInstanceStatus.DRAFT) =
        ChecklistInstanceDetail(
            id = checklistId,
            programmeChecklistId = programmeChecklistId,
            status = status,
            type = ProgrammeChecklistType.CONTRACTING,
            name = "name",
            relatedToId = projectId,
            creatorEmail = creatorEmail,
            creatorId = creatorId,
            finishedDate = null,
            minScore = BigDecimal(0),
            maxScore = BigDecimal(10),
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
                    2,
                    TextInputMetadata("Question to be answered", "Label", 2000),
                    TextInputInstanceMetadata("Explanation")
                )
            )
        )

    private val optionsToggleComponentInstance = ChecklistComponentInstance(
        4L,
        ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
        0,
        OptionsToggleMetadata("Question to be answered", "yes", "no", "", ""),
        OptionsToggleInstanceMetadata("yes", "A".repeat(5001))
    )

    private val textInputComponentInstance = ChecklistComponentInstance(
        4L,
        ProgrammeChecklistComponentType.TEXT_INPUT,
        0,
        TextInputMetadata("Question to be answered", "Label", 2000),
        TextInputInstanceMetadata("A".repeat(3000))
    )

    private val scoreComponentInstance = ChecklistComponentInstance(
        5L,
        ProgrammeChecklistComponentType.SCORE,
        0,
        ScoreMetadata("Question to be answered", BigDecimal(1)),
        ScoreInstanceMetadata(BigDecimal(5), "A".repeat(5001))
    )

    private val contractingChecklistDetailWithErrorOnTextInput = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        creatorEmail = creatorEmail,
        creatorId = creatorId,
        relatedToId = projectId,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = false,
        components = mutableListOf(textInputComponentInstance)
    )

    private fun contractingChecklistInstance(
        status: ChecklistInstanceStatus,
        creatorEmail: String = "user@applicant.dev"
    ) = ChecklistInstance(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = status,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        relatedToId = projectId,
        creatorEmail = creatorEmail,
        finishedDate = null,
        consolidated = false,
        visible = true
    )

    private val contractingChecklistDetailWithErrorOnScore = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        relatedToId = projectId,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = true,
        creatorEmail = creatorEmail,
        creatorId = creatorId,
        components = mutableListOf(scoreComponentInstance)
    )

    private val contractingChecklistDetailWithErrorOnOptionsToggle = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CONTRACTING,
        name = "name",
        relatedToId = projectId,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        consolidated = false,
        visible = true,
        creatorEmail = creatorEmail,
        creatorId = creatorId,
        allowsDecimalScore = false,
        components = mutableListOf(optionsToggleComponentInstance)
    )

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @MockK
    lateinit var securityService: SecurityService

    lateinit var updateContractingChecklistInstance: UpdateContractingChecklistInstance

    lateinit var generalValidator: GeneralValidatorService

    lateinit var checklistInstanceValidator: ChecklistInstanceValidator

    @BeforeEach
    fun setup() {
        clearMocks(persistence, auditPublisher)
        MockKAnnotations.init(this)
        every { userAuthorization.hasPermissionForProject(any(), any()) } returns false
        generalValidator = GeneralValidatorDefaultImpl()
        checklistInstanceValidator = mockk()
        checklistInstanceValidator = ChecklistInstanceValidator(generalValidator)
        updateContractingChecklistInstance =
            UpdateContractingChecklistInstance(
                persistence,
                auditPublisher,
                checklistInstanceValidator,
                userAuthorization
            )
    }

    @Test
    fun `update - successful`() {
        every { userAuthorization.getUser().email } returns creatorEmail
        every { persistence.update(contractingChecklistDetail) } returns contractingChecklistDetail
        every {
            persistence.getChecklistDetail(
                contractingChecklistDetail.id,
                ProgrammeChecklistType.CONTRACTING,
                projectId
            )
        } returns contractingChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )
        Assertions.assertThat(updateContractingChecklistInstance.update(projectId, contractingChecklistDetail))
            .isEqualTo(contractingChecklistDetail)
    }

    @Test
    fun `update - failed (user is not the owner & the attempt is to set to FINISHED status)`() {
        every { userAuthorization.getUser().email } returns notCreatorEmail
        every { persistence.update(contractingChecklistDetail) } returns contractingChecklistDetail
        every {
            persistence.getChecklistDetail(
                contractingChecklistDetail.id,
                ProgrammeChecklistType.CONTRACTING,
                projectId
            )
        } returns contractingChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT
        )

        assertThrows<UpdateContractingChecklistInstanceStatusNotAllowedException> {
            updateContractingChecklistInstance.update(
                projectId, contractingChecklistInstanceDetail(
                    ChecklistInstanceStatus.FINISHED
                )
            )
        }
    }

    @Test
    fun `change status (should trigger an audit log)`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.update(contractingChecklistDetail) } returns contractingChecklistDetail
        every { userAuthorization.getUser() } returns user
        every { securityService.getUserIdOrThrow() } returns user.id
        every {
            persistence.getChecklistSummary(
                checklistId,
                ProgrammeChecklistType.CONTRACTING,
                projectId
            )
        } returns contractingChecklistInstance(
            ChecklistInstanceStatus.DRAFT
        )
        every {
            persistence.changeStatus(
                checklistId,
                ChecklistInstanceStatus.FINISHED
            )
        } returns contractingChecklistInstance(
            ChecklistInstanceStatus.FINISHED
        )

        updateContractingChecklistInstance.changeStatus(projectId, checklistId, ChecklistInstanceStatus.FINISHED)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CHECKLIST_STATUS_CHANGE,
                project = AuditProject(id = projectId.toString()),
                description = "Checklist '${contractingChecklistDetail.id}' " +
                        "type '${contractingChecklistDetail.type}' " +
                        "name '${contractingChecklistDetail.name}' " +
                        "in 'Contract monitoring' changed status from 'DRAFT' to 'FINISHED'"
            )
        )
    }

    @Test
    fun `update - contractingChecklistDetail is already in FINISHED status`() {
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CONTRACTING,
                projectId
            )
        } returns contractingChecklistDetail
        assertThrows<UpdateContractingChecklistInstanceStatusNotAllowedException> {
            updateContractingChecklistInstance.update(
                projectId, contractingChecklistInstanceDetail(
                    ChecklistInstanceStatus.FINISHED
                )
            )
        }
    }

    @Test
    fun `update - text input component max length exception`() {
        every { userAuthorization.getUser().email } returns creatorEmail
        every {
            persistence.getChecklistDetail(
                contractingChecklistDetailWithErrorOnTextInput.id,
                ProgrammeChecklistType.CONTRACTING,
                projectId
            )
        } returns contractingChecklistDetailWithErrorOnTextInput

        assertThrows<AppInputValidationException> {
            updateContractingChecklistInstance.update(
                projectId,
                contractingChecklistDetailWithErrorOnTextInput
            )
        }
    }

    @Test
    fun `update - options toggle justification field max length exception`() {
        every {
            persistence.getChecklistDetail(
                contractingChecklistDetailWithErrorOnOptionsToggle.id,
                ProgrammeChecklistType.CONTRACTING,
                projectId
            )
        } returns contractingChecklistDetailWithErrorOnOptionsToggle
        every { persistence.update(contractingChecklistDetailWithErrorOnOptionsToggle) } returns contractingChecklistDetailWithErrorOnOptionsToggle

        assertThrows<AppInputValidationException> {
            updateContractingChecklistInstance.update(
                projectId,
                contractingChecklistDetailWithErrorOnOptionsToggle
            )
        }
    }

    @Test
    fun `update - score justification field max length exception`() {
        every { userAuthorization.getUser().email } returns creatorEmail
        every {
            persistence.getChecklistDetail(
                contractingChecklistDetailWithErrorOnScore.id,
                ProgrammeChecklistType.CONTRACTING,
                projectId
            )
        } returns contractingChecklistDetailWithErrorOnScore

        assertThrows<AppInputValidationException> {
            updateContractingChecklistInstance.update(
                projectId,
                contractingChecklistDetailWithErrorOnScore
            )
        }
    }
}
