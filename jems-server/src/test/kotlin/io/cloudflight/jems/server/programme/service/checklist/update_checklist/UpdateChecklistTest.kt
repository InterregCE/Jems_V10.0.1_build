package io.cloudflight.jems.server.programme.service.checklist.update_checklist

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.ChecklistTemplateValidator
import io.cloudflight.jems.server.programme.service.checklist.ChecklistTemplateValidator.Companion.MAX_NUMBER_OF_CHECKLIST_COMPONENTS
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.update.ChecklistLockedException
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateProgrammeChecklist
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

internal class UpdateChecklistTest : UnitTest() {

    companion object {

        private const val CHECKLIST_ID = 100L

        private fun getChecklist(locked: Boolean = false) = ProgrammeChecklistDetail(
            id = CHECKLIST_ID,
            type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
            locked = locked,
            components = emptyList()
        )
    }



    @MockK
    lateinit var persistence: ProgrammeChecklistPersistence

    @MockK
    lateinit var checklistInstancePersistence: ChecklistInstancePersistence

    @RelaxedMockK
    lateinit var checklistTemplateValidator: ChecklistTemplateValidator

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updateProgrammeChecklist: UpdateProgrammeChecklist

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
    }

    @Test
    fun `update - successfully`() {
        val checkList = getChecklist()
        every { checklistTemplateValidator.validateInput(checkList) } returns Unit
        every { checklistInstancePersistence.countAllByChecklistTemplateId(CHECKLIST_ID) } returns 0
        every { persistence.createOrUpdate(checkList) } returns checkList
        Assertions.assertThat(updateProgrammeChecklist.update(checkList)).isEqualTo(checkList)
    }

    @Test
    fun `update - max amount of components`() {
        val checkList = getChecklist()
        val exception = AppInputValidationException(mapOf(
            "components" to I18nMessage(i18nKey = "common.error.field.max.size"),
        ))
        val listMock = ArrayList(Collections.nCopies(101, mockk<ProgrammeChecklistComponent>()))
        val toBeUpdated = ProgrammeChecklistDetail(
            id = CHECKLIST_ID,
            type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
            locked = false,
            components = listMock
        )


        every { generalValidator.maxSize(listMock, MAX_NUMBER_OF_CHECKLIST_COMPONENTS, "components") } returns
            (mapOf(
                "components" to I18nMessage(i18nKey = "common.error.field.max.size"),
            ))
        every { checklistTemplateValidator.validateInput(toBeUpdated) } throws exception

        every { persistence.createOrUpdate(checkList) } returns checkList
        assertThrows<AppInputValidationException> { updateProgrammeChecklist.update(toBeUpdated) }
        verify(exactly = 1) { checklistTemplateValidator.validateInput(toBeUpdated) }
    }


    @Test
    fun `update - checklist locked`() {
        val listMock = ArrayList(Collections.nCopies(10, mockk<ProgrammeChecklistComponent>()))
        val toBeUpdated = ProgrammeChecklistDetail(
            id = CHECKLIST_ID,
            type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
            locked = false,
            components = listMock
        )

        every { checklistTemplateValidator.validateInput(toBeUpdated)} returns Unit
        every { checklistInstancePersistence.countAllByChecklistTemplateId(CHECKLIST_ID) } returns 1
        assertThrows<ChecklistLockedException> { updateProgrammeChecklist.update(toBeUpdated) }
    }

}
