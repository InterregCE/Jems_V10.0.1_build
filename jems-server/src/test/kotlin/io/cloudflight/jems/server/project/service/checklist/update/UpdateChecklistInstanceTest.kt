package io.cloudflight.jems.server.project.service.checklist.update

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.controller.ApplicationFormConfigurationControllerTest
import io.cloudflight.jems.server.call.service.update_application_form_field_configuration.UpdateApplicationFormFieldConfigurationsException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.*
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstanceValidator
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.utils.user
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class UpdateChecklistInstanceTest : UnitTest() {

    private val CHECKLIST_ID = 100L
    private val RELATED_TO_ID = 2L
    private val PROGRAMME_CHECKLIST_ID = 4L

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
            finishedDate = null,
            consolidated = false,
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

    private val checkLisDetailWithError = ChecklistInstanceDetail(
        id = CHECKLIST_ID,
        programmeChecklistId = PROGRAMME_CHECKLIST_ID,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        creatorEmail = "a@a",
        relatedToId = RELATED_TO_ID,
        finishedDate = null,
        consolidated = false,
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
    )

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var userAuthorization: UserAuthorization

    lateinit var updateChecklistInstance: UpdateChecklistInstance

    lateinit var checklistInstanceValidator: ChecklistInstanceValidator

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
        MockKAnnotations.init(this)
        checklistInstanceValidator = mockk()
        every { userAuthorization.hasPermissionForProject(any(), any()) } returns false
        updateChecklistInstance = UpdateChecklistInstance(persistence, auditPublisher, checklistInstanceValidator, userAuthorization)
    }

    @Test
    fun `update - successfully`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { checklistInstanceValidator.validateChecklistComponents(checkLisDetail.components) } returns Unit
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { persistence.update(checkLisDetail) } returns checkLisDetail
        every { persistence.getChecklistDetail(checkLisDetail.id) } returns checklistInstanceDetail(ChecklistInstanceStatus.DRAFT)
        Assertions.assertThat(updateChecklistInstance.update(checkLisDetail))
            .isEqualTo(checkLisDetail)
    }

    @Test
    fun `change status should trigger an audit log`() {
        val auditSlot = slot<AuditCandidateEvent>()

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
        val auditSlot = slot<AuditCandidateEvent>()
        val textInputInstanceMetadata = textInputComponentInstance.instanceMetadata as TextInputInstanceMetadata
        val exception = AppInputValidationException(
            mapOf(
                textInputInstanceMetadata.explanation to I18nMessage(i18nKey = "common.error.field.max.length")
            )
        )
        every { checklistInstanceValidator.validateChecklistComponents(checkLisDetailWithError.components) } throws exception
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.update(checkLisDetailWithError) } returns checkLisDetailWithError
        every { persistence.getChecklistDetail(checkLisDetail.id) } returns checklistInstanceDetail(ChecklistInstanceStatus.DRAFT)
        assertThrows<AppInputValidationException> { updateChecklistInstance.update(checkLisDetailWithError) }
    }

    @Test
    fun `update - options toggle justification field max length exception`() {
        val auditSlot = slot<AuditCandidateEvent>()
        val justificationField =
            (optionsToggleComponentInstance.instanceMetadata as OptionsToggleInstanceMetadata).justification ?: ""
        val exception = AppInputValidationException(
            mapOf(justificationField to I18nMessage(i18nKey = "common.error.field.max.length"))
        )
        every { checklistInstanceValidator.validateChecklistComponents(checkLisDetailWithError.components) } throws exception
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.update(checkLisDetailWithError) } returns checkLisDetailWithError
        every { persistence.getChecklistDetail(checkLisDetail.id) } returns checklistInstanceDetail(ChecklistInstanceStatus.DRAFT)
        assertThrows<AppInputValidationException> { updateChecklistInstance.update(checkLisDetailWithError) }
    }

}
