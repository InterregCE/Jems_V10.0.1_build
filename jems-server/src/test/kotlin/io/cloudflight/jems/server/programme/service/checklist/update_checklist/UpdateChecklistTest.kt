package io.cloudflight.jems.server.programme.service.checklist.update_checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.ChecklistTemplateValidator
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.ScoreMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.programme.service.checklist.update.IllegalUpdateProgrammeChecklistException
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateProgrammeChecklist
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

internal class UpdateChecklistTest : UnitTest() {

    companion object {

        private const val CHECKLIST_ID = 100L

        private fun getChecklist(locked: Boolean = false, components: List<ProgrammeChecklistComponent>? = emptyList()) = ProgrammeChecklistDetail(
            id = CHECKLIST_ID,
            type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            minScore = BigDecimal(0),
            maxScore = BigDecimal(10),
            allowsDecimalScore = false,
            lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
            locked = locked,
            components = components
        )
    }

    private val textInputComponentWithError = ProgrammeChecklistComponent(
        id = CHECKLIST_ID,
        type = ProgrammeChecklistComponentType.TEXT_INPUT,
        position = 0,
        metadata = TextInputMetadata("A".repeat(3000), "Label", 2000),
    )

    private val scoreInputComponent = ProgrammeChecklistComponent(
        id = CHECKLIST_ID,
        type = ProgrammeChecklistComponentType.SCORE,
        position = 0,
        metadata = ScoreMetadata("Is this a test?"),
    )

    @RelaxedMockK
    lateinit var persistence: ProgrammeChecklistPersistence

    @RelaxedMockK
    lateinit var checklistInstancePersistence: ChecklistInstancePersistence

    @RelaxedMockK
    lateinit var applicationEventPublisher: ApplicationEventPublisher

    lateinit var checklistTemplateValidator: ChecklistTemplateValidator

    lateinit var generalValidator: GeneralValidatorService

    lateinit var updateProgrammeChecklist: UpdateProgrammeChecklist

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
        MockKAnnotations.init(this)
        generalValidator = GeneralValidatorDefaultImpl()
        checklistTemplateValidator = ChecklistTemplateValidator(generalValidator)
        updateProgrammeChecklist = UpdateProgrammeChecklist(persistence, checklistInstancePersistence, checklistTemplateValidator, applicationEventPublisher)
    }

    @Test
    fun `update - successfully`() {
        val checkList = getChecklist()

        every { checklistInstancePersistence.countAllByChecklistTemplateId(CHECKLIST_ID) } returns 0
        every { persistence.updateChecklist(checkList) } returns checkList
        Assertions.assertThat(updateProgrammeChecklist.update(checkList)).isEqualTo(checkList)
    }

    @Test
    fun `update - max amount of components`() {
        val checkList = getChecklist(false, ArrayList(Collections.nCopies(201, mockk<ProgrammeChecklistComponent>())))

        every { persistence.updateChecklist(checkList) } returns checkList
        assertThrows<AppInputValidationException> { updateProgrammeChecklist.update(checkList) }
    }

    @Test
    fun `update - max length validation error`() {
        val checkList = getChecklist(false, mutableListOf(textInputComponentWithError))

        every { checklistInstancePersistence.countAllByChecklistTemplateId(CHECKLIST_ID) } returns 0
        assertThrows<AppInputValidationException> { updateProgrammeChecklist.update(checkList) }
    }

    @Test
    fun `update - locked checklist update name validation error`() {
        val existingChecklist = getChecklist(true, mutableListOf(textInputComponentWithError))
        val updatedChecklist = getChecklist(true, mutableListOf(scoreInputComponent))

        every { checklistInstancePersistence.countAllByChecklistTemplateId(CHECKLIST_ID) } returns 1
        every { persistence.getChecklistDetail(CHECKLIST_ID) } returns existingChecklist
        assertThrows<IllegalUpdateProgrammeChecklistException> { updateProgrammeChecklist.update(updatedChecklist) }
    }
}
