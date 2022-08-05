package io.cloudflight.jems.server.programme.service.checklist.create_checklist

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.ChecklistTemplateValidator
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.create.CreateProgrammeChecklist
import io.cloudflight.jems.server.programme.service.checklist.create.CreateProgrammeChecklist.Companion.MAX_NUMBER_OF_CHECKLIST_COMPONENTS
import io.cloudflight.jems.server.programme.service.checklist.create.MaxAmountOfProgrammeChecklistReached
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Collections

internal class CreateChecklistTest : UnitTest() {

    private val CHECKLIST_ID = 100L

    private val checkList = ProgrammeChecklistDetail(
        id = CHECKLIST_ID,
        type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
        name = "name",
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
        locked = false,
        components = emptyList()
    )

    @MockK
    lateinit var persistence: ProgrammeChecklistPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @RelaxedMockK
    lateinit var checklistTemplateValidator: ChecklistTemplateValidator

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var createProgrammeChecklist: CreateProgrammeChecklist

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
    }

    @Test
    fun `create - successfully`() {
        every { persistence.countAll() } returns 1
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { persistence.createOrUpdate(checkList) } returns checkList
        Assertions.assertThat(createProgrammeChecklist.create(checkList)).isEqualTo(checkList)
    }

    @Test
    fun `create - max amount reached`() {
        every { persistence.countAll() } returns 101
        assertThrows<MaxAmountOfProgrammeChecklistReached> { createProgrammeChecklist.create(checkList) }
    }

    @Test
    fun `create - max amount of components`() {
        val listMock = ArrayList(Collections.nCopies(101, mockk<ProgrammeChecklistComponent>()))
        val toBeCreated = ProgrammeChecklistDetail(
            id = CHECKLIST_ID,
            type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT,
            name = "name",
            minScore = BigDecimal(0),
            maxScore = BigDecimal(10),
            allowsDecimalScore = false,
            lastModificationDate = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 10, ZoneId.systemDefault()),
            locked = false,
            components = listMock
        )
        val errors = mapOf(
            "components" to I18nMessage(i18nKey = "common.error.field.max.size"),
        )
        every { generalValidator.maxSize(listMock, MAX_NUMBER_OF_CHECKLIST_COMPONENTS, "components") } returns errors
        every { checklistTemplateValidator.validateNewChecklist(toBeCreated) } throws AppInputValidationException(errors)
        assertThrows<AppInputValidationException> { createProgrammeChecklist.create(toBeCreated) }
        verify(exactly = 1) { checklistTemplateValidator.validateNewChecklist((toBeCreated)) }
    }
}
