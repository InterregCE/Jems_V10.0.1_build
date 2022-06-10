package io.cloudflight.jems.server.project.service.checklist.update

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
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

internal class UpdateChecklistInstanceTest : UnitTest() {

    private val CHECKLIST_ID = 100L
    private val RELATED_TO_ID = 2L
    private val PROGRAMME_CHECKLIST_ID = 4L
    private val CREATOR_ID = 1L

    private val checkLisDetail = checklistInstanceDetail()

    private fun checklistInstanceDetail(status: ChecklistInstanceStatus = ChecklistInstanceStatus.DRAFT) =
        ChecklistInstanceDetail(
            id = CHECKLIST_ID,
            programmeChecklistId = PROGRAMME_CHECKLIST_ID,
            status = status,
            type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            relatedToId = RELATED_TO_ID,
            creatorEmail = "a@a",
            creatorId = CREATOR_ID,
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

    private val checkLisDetailWithErrorOnTextInput = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "a@a",
        creatorId = CREATOR_ID,
        relatedToId = RELATED_TO_ID,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = false,
        components = mutableListOf(textInputComponentInstance)
    )

    private fun checklistInstance(status: ChecklistInstanceStatus, creatorEmail: String = "user@applicant.dev") = ChecklistInstance(
        id = CHECKLIST_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = status,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        relatedToId = RELATED_TO_ID,
        creatorEmail = creatorEmail,
        finishedDate = null,
        consolidated = false,
        visible = true
    )

    private val checkLisDetailWithErrorOnScore = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        relatedToId = RELATED_TO_ID,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = true,
        creatorEmail = "a@a",
        creatorId = CREATOR_ID,
        components = mutableListOf(scoreComponentInstance)
    )

    private val checkLisDetailWithErrorOnOptionsToggle = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        relatedToId = RELATED_TO_ID,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        consolidated = false,
        visible = true,
        creatorEmail = "a@a",
        creatorId = CREATOR_ID,
        allowsDecimalScore = false,
        components = mutableListOf(optionsToggleComponentInstance)
    )

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var userAuthorization: UserAuthorization

    lateinit var updateChecklistInstance: UpdateChecklistInstance

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
        updateChecklistInstance = UpdateChecklistInstance(persistence, auditPublisher, checklistInstanceValidator, userAuthorization)
    }

    @Test
    fun `update - successfully`() {
        every { persistence.update(checkLisDetail) } returns checkLisDetail
        every { persistence.getChecklistDetail(checkLisDetail.id) } returns checklistInstanceDetail(ChecklistInstanceStatus.DRAFT)
        Assertions.assertThat(updateChecklistInstance.update(checkLisDetail))
            .isEqualTo(checkLisDetail)
    }

    @Test
    fun `change status should trigger an audit log`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.update(checkLisDetail) } returns checkLisDetail
        every { userAuthorization.getUser() } returns user
        every { persistence.getChecklistSummary(CHECKLIST_ID) } returns checklistInstance(ChecklistInstanceStatus.DRAFT)
        every { persistence.changeStatus(CHECKLIST_ID, ChecklistInstanceStatus.FINISHED) } returns checklistInstance(ChecklistInstanceStatus.FINISHED)

        updateChecklistInstance.changeStatus(CHECKLIST_ID, ChecklistInstanceStatus.FINISHED)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.ASSESSMENT_CHECKLIST_STATUS_CHANGE,
                project = AuditProject(id = checkLisDetail.relatedToId.toString()),
                description = "[" + checkLisDetail.id + "] [" + checkLisDetail.type + "]" +
                    " [" + checkLisDetail.name + "]" + " status changed from [DRAFT] to [FINISHED]"
            )
        )
    }

    @Test
    fun `update - checkLisDetail is already in FINISHED status`() {
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns checkLisDetail
        assertThrows<UpdateChecklistInstanceStatusNotAllowedException> {
            updateChecklistInstance.update(checklistInstanceDetail(ChecklistInstanceStatus.FINISHED))
        }
    }

    @Test
    fun `change status to FINISHED only allowed to assessor`() {
        every { userAuthorization.getUser() } returns user
        every { persistence.getChecklistSummary(CHECKLIST_ID) } returns checklistInstance(ChecklistInstanceStatus.DRAFT, "different@email")

        assertThrows<UpdateChecklistInstanceStatusNotAllowedException> {
            updateChecklistInstance.changeStatus(CHECKLIST_ID, ChecklistInstanceStatus.FINISHED)
        }
    }

    @Test
    fun `change status to DRAFT only allowed to consolidator`() {
        every { userAuthorization.getUser() } returns user
        every { persistence.getChecklistSummary(CHECKLIST_ID) } returns checklistInstance(ChecklistInstanceStatus.FINISHED)

        assertThrows<UpdateChecklistInstanceStatusNotAllowedException> {
            updateChecklistInstance.changeStatus(CHECKLIST_ID, ChecklistInstanceStatus.DRAFT)
        }
    }

    @Test
    fun `update - text input component max length exception`() {
        every { persistence.getChecklistDetail(checkLisDetailWithErrorOnTextInput.id) } returns checkLisDetailWithErrorOnTextInput

        assertThrows<AppInputValidationException> { updateChecklistInstance.update(checkLisDetailWithErrorOnTextInput) }
    }

    @Test
    fun `update - options toggle justification field max length exception`() {
        every { persistence.getChecklistDetail(checkLisDetailWithErrorOnOptionsToggle.id) } returns checkLisDetailWithErrorOnOptionsToggle
        every { persistence.update(checkLisDetailWithErrorOnOptionsToggle) } returns checkLisDetailWithErrorOnOptionsToggle

        assertThrows<AppInputValidationException> { updateChecklistInstance.update(checkLisDetailWithErrorOnOptionsToggle) }
    }

    @Test
    fun `update - score justification field max length exception`() {
        every { persistence.getChecklistDetail(checkLisDetailWithErrorOnScore.id) } returns checkLisDetailWithErrorOnScore

        assertThrows<AppInputValidationException> { updateChecklistInstance.update(checkLisDetailWithErrorOnScore) }
    }

    @Test
    fun `update selection - set visible`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        val checklist = checklistInstance(ChecklistInstanceStatus.FINISHED)
        every { persistence.getChecklistSummary(CHECKLIST_ID) } returns checklist
        every { persistence.updateSelection(mapOf(CHECKLIST_ID to true)) } returns listOf(checklist)

        updateChecklistInstance.updateSelection(mapOf(CHECKLIST_ID to true))

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.ASSESSMENT_CHECKLIST_VISIBILITY_CHANGE,
                project = AuditProject(id = checklist.relatedToId.toString()),
                description = "[" + checklist.id + "] [" + checklist.type + "]" +
                    " [" + checklist.name + "]" + " set to visibility true"
            )
        )
    }

    @Test
    fun `update selection - set invisible`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        val checklist = checklistInstance(ChecklistInstanceStatus.FINISHED).copy(visible = false)
        every { persistence.getChecklistSummary(CHECKLIST_ID) } returns checklist
        every { persistence.updateSelection(mapOf(CHECKLIST_ID to false)) } returns listOf(checklist)

        updateChecklistInstance.updateSelection(mapOf(CHECKLIST_ID to false))

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.ASSESSMENT_CHECKLIST_VISIBILITY_CHANGE,
                project = AuditProject(id = checklist.relatedToId.toString()),
                description = "[" + checklist.id + "] [" + checklist.type + "]" +
                    " [" + checklist.name + "]" + " set to visibility false"
            )
        )
    }

}
