package io.cloudflight.jems.server.project.service.checklist.update.control

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

internal class UpdateControlChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val programmeChecklistId = 4L
    private val creatorId = 1L
    private val partnerId = 5L
    private val reportId = 6L
    private val creatorEmail = "a@a"

    private val controlCheckLisDetail = controlChecklistInstanceDetail()

    private fun controlChecklistInstanceDetail(status: ChecklistInstanceStatus = ChecklistInstanceStatus.DRAFT) =
        ChecklistInstanceDetail(
            id = checklistId,
            programmeChecklistId = programmeChecklistId,
            status = status,
            type = ProgrammeChecklistType.CONTROL,
            name = "name",
            relatedToId = reportId,
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

    private val controlCheckLisDetailWithErrorOnTextInput = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        creatorEmail = creatorEmail,
        creatorId = creatorId,
        relatedToId = reportId,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = false,
        components = mutableListOf(textInputComponentInstance)
    )

    private fun controlChecklistInstance(status: ChecklistInstanceStatus, creatorEmail: String = "user@applicant.dev") = ChecklistInstance(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = status,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        relatedToId = reportId,
        creatorEmail = creatorEmail,
        finishedDate = null,
        consolidated = false,
        visible = true
    )

    private val controlCheckLisDetailWithErrorOnScore = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        relatedToId = reportId,
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

    private val controlCheckLisDetailWithErrorOnOptionsToggle = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = programmeChecklistId,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        relatedToId = reportId,
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

    lateinit var updateControlChecklistInstance: UpdateControlChecklistInstance

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
        updateControlChecklistInstance =
            UpdateControlChecklistInstance(persistence, auditPublisher, checklistInstanceValidator, userAuthorization, securityService)
    }

    @Test
    fun `update - successfully`() {
        every { userAuthorization.getUser().email } returns creatorEmail
        every { persistence.update(controlCheckLisDetail) } returns controlCheckLisDetail
        every { persistence.getChecklistDetail(controlCheckLisDetail.id) } returns controlChecklistInstanceDetail(
            ChecklistInstanceStatus.DRAFT)
        Assertions.assertThat(updateControlChecklistInstance.update(partnerId, reportId, controlCheckLisDetail))
            .isEqualTo(controlCheckLisDetail)
    }

    @Test
    fun `change status (should trigger an audit log)`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.update(controlCheckLisDetail) } returns controlCheckLisDetail
        every { userAuthorization.getUser() } returns user
        every { securityService.getUserIdOrThrow() } returns user.id
        every { persistence.getChecklistSummary(checklistId) } returns controlChecklistInstance(ChecklistInstanceStatus.DRAFT)
        every { persistence.changeStatus(checklistId, ChecklistInstanceStatus.FINISHED) } returns controlChecklistInstance(
            ChecklistInstanceStatus.FINISHED)

        updateControlChecklistInstance.changeStatus(partnerId, reportId, checklistId, ChecklistInstanceStatus.FINISHED)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CONTROL_CHECKLIST_STATUS_CHANGE,
                project = AuditProject(id = controlCheckLisDetail.relatedToId.toString()),
                description = "Checklist [${controlCheckLisDetail.id}] type [${controlCheckLisDetail.type}] name [${controlCheckLisDetail.name}] " +
                        "for [${partnerId}] and [${reportId}] changed status from [DRAFT] to [FINISHED] by [${userAuthorization.getUser().id}]"
            )
        )
    }

    @Test
    fun `update - controlChecklistDetail is already in FINISHED status`() {
        every { persistence.getChecklistDetail(checklistId) } returns controlCheckLisDetail
        assertThrows<UpdateControlChecklistInstanceStatusNotAllowedException> {
            updateControlChecklistInstance.update(partnerId, reportId, controlChecklistInstanceDetail(ChecklistInstanceStatus.FINISHED))
        }
    }

    @Test
    fun `update - text input component max length exception`() {
        every { userAuthorization.getUser().email } returns creatorEmail
        every { persistence.getChecklistDetail(controlCheckLisDetailWithErrorOnTextInput.id) } returns controlCheckLisDetailWithErrorOnTextInput

        assertThrows<AppInputValidationException> { updateControlChecklistInstance.update(partnerId, reportId, controlCheckLisDetailWithErrorOnTextInput) }
    }

    @Test
    fun `update - options toggle justification field max length exception`() {
        every { persistence.getChecklistDetail(controlCheckLisDetailWithErrorOnOptionsToggle.id) } returns controlCheckLisDetailWithErrorOnOptionsToggle
        every { persistence.update(controlCheckLisDetailWithErrorOnOptionsToggle) } returns controlCheckLisDetailWithErrorOnOptionsToggle

        assertThrows<AppInputValidationException> { updateControlChecklistInstance.update(partnerId, reportId, controlCheckLisDetailWithErrorOnOptionsToggle) }
    }

    @Test
    fun `update - score justification field max length exception`() {
        every { userAuthorization.getUser().email } returns creatorEmail
        every { persistence.getChecklistDetail(controlCheckLisDetailWithErrorOnScore.id) } returns controlCheckLisDetailWithErrorOnScore

        assertThrows<AppInputValidationException> { updateControlChecklistInstance.update(partnerId, reportId, controlCheckLisDetailWithErrorOnScore) }
    }
}