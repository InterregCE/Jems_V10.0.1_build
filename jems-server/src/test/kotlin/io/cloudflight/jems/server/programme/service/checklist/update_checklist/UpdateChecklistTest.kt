package io.cloudflight.jems.server.programme.service.checklist.update_checklist

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.update.UpdateProgrammeChecklist.Companion.MAX_NUMBER_OF_CHECKLIST_COMPONENTS
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

    private val CHECKLIST_ID = 100L

    private val checkList = ProgrammeChecklistDetail(
        id = CHECKLIST_ID,
        type = ProgrammeChecklistType.ELIGIBILITY,
        name = "name",
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        components = emptyList()
    )

    @MockK
    lateinit var persistence: ProgrammeChecklistPersistence

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
        every { persistence.createOrUpdate(checkList) } returns checkList
        Assertions.assertThat(updateProgrammeChecklist.update(checkList)).isEqualTo(checkList)
    }

    @Test
    fun `update - max amount of components`() {
        every { persistence.createOrUpdate(checkList) } returns checkList
        val listMock = ArrayList(Collections.nCopies(101, mockk<ProgrammeChecklistComponent>()))
        every { generalValidator.maxSize(listMock, MAX_NUMBER_OF_CHECKLIST_COMPONENTS, "components") } returns
            (mapOf(
                "components" to I18nMessage(i18nKey = "common.error.field.max.size"),
            ))
        val toBeUpdated = ProgrammeChecklistDetail(
            id = CHECKLIST_ID,
            type = ProgrammeChecklistType.ELIGIBILITY,
            name = "name",
            lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
            components = listMock
        )
        assertThrows<AppInputValidationException> { updateProgrammeChecklist.update(toBeUpdated) }
        verify(exactly = 1) { generalValidator.maxSize(listMock, MAX_NUMBER_OF_CHECKLIST_COMPONENTS, "components") }
    }

}
