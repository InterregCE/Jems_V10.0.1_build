package io.cloudflight.jems.server.programme.service.checklist

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.mockk.MockKAnnotations
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.ZoneId
import java.time.ZonedDateTime

internal class ChecklistTemplateValidatorTest : UnitTest() {

    private val checklistId = 1L
    private val headlineComponent = ProgrammeChecklistComponent(
        id = 1L,
        type = ProgrammeChecklistComponentType.HEADLINE,
        position = 1,
        metadata = HeadlineMetadata(value = "headline")
    )
    private val components = mutableListOf(headlineComponent)

    private val newCheckList = ProgrammeChecklistDetail(
        id = 0L,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false,
        components = components
    )

    private val checkList = ProgrammeChecklistDetail(
        id = checklistId,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false,
        components = emptyList()
    )

    lateinit var generalValidator: GeneralValidatorService

    lateinit var checklistTemplateValidator: ChecklistTemplateValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        generalValidator = GeneralValidatorDefaultImpl()
        checklistTemplateValidator = ChecklistTemplateValidator(generalValidator)
    }

    @Test
    fun `validate new checklist fails with id`() {
        assertThrows<AppInputValidationException> {
            checklistTemplateValidator.validateNewChecklist(checkList)
        }
    }

    @Test
    fun `validate new checklist fails with max components`() {
        assertDoesNotThrow {
            checklistTemplateValidator.validateNewChecklist(newCheckList)
        }

        components.addAll(List(100) { index ->
            ProgrammeChecklistComponent(
                id = index.toLong(),
                type = ProgrammeChecklistComponentType.HEADLINE,
                position = index,
                metadata = HeadlineMetadata(value = "headline")
            )
        })

        assertThrows<AppInputValidationException> {
            checklistTemplateValidator.validateNewChecklist(newCheckList)
        }
    }

    @Test
    fun `validate updating checklist`() {
        assertDoesNotThrow {
            checklistTemplateValidator.validateInput(checkList)
        }
    }
}
