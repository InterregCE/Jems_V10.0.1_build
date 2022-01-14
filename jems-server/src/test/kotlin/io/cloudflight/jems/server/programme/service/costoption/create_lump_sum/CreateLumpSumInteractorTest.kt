package io.cloudflight.jems.server.programme.service.costoption.create_lump_sum

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase.Implementation
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class CreateLumpSumInteractorTest : UnitTest() {

    private val inputErrorMap = mapOf("error" to I18nMessage("error.key"))

    @MockK
    lateinit var persistence: ProgrammeLumpSumPersistence

    @MockK
    lateinit var auditService: AuditService

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var createLumpSum: CreateLumpSum

    @BeforeEach
    fun reset() {
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(
            inputErrorMap
        )
    }

    @Test
    fun `create lump sum - invalid`() {
        every { persistence.getCount() } returns 15
        val wrongLumpSum = ProgrammeLumpSum(
            id = 0L,
            name = setOf(InputTranslation(SystemLanguage.EN, " ")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1")),
            cost = null,
            splittingAllowed = true,
            phase = null,
            categories = setOf(OfficeAndAdministrationCosts),
        )
        val ex = assertThrows<LumpSumIsInvalid> { createLumpSum.createLumpSum(wrongLumpSum) }
        assertThat(ex.formErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "cost" to I18nMessage(i18nKey = "lump.sum.out.of.range"),
            "categories" to I18nMessage(i18nKey = "programme.lumpSum.categories.min.2"),
        ))
    }

    @Test
    fun `create lump sum - reached max allowed amount`() {
        every { persistence.getCount() } returns 100
        val lumpSum = ProgrammeLumpSum(
            id = 0L,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        assertThrows<MaxAllowedLumpSumsReached> { createLumpSum.createLumpSum(lumpSum) }
    }

    @Test
    fun `create lump sum - OK`() {
        every { persistence.getCount() } returns 5
        every { persistence.createLumpSum(any()) } returnsArgument 0
        val lumpSum = ProgrammeLumpSum(
            id = 0L,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val auditSlot = slot<AuditCandidate>()
        every { auditService.logEvent(capture(auditSlot)) } answers {}

        assertThat(createLumpSum.createLumpSum(lumpSum)).isEqualTo(lumpSum.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_LUMP_SUM_ADDED,
            description = "Programme lump sum (id=0) '[EN=LS1]' has been added" // null will be real ID from DB sequence
        ))
    }

    @Test
    fun `create lump sum - wrong ID filled in`() {
        every { persistence.getCount() } returns 10
        val lumpSum = ProgrammeLumpSum(
            id = 1L,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
        )

        assertThrows<IdHasToBeNull>("when creating id cannot be filled in") {
            createLumpSum.createLumpSum(lumpSum) }
    }

    @Test
    fun `create lump sum - check if name length is validated`() {
        val lumpSum = ProgrammeLumpSum(
            id = 1L,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
        )
        val name = setOf(InputTranslation(SystemLanguage.SK, getStringOfLength(51)))
        every {
            generalValidator.maxLength(name, 50, "name")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            createLumpSum.createLumpSum(lumpSum.copy(name = name))
        }
        verify(exactly = 1) { generalValidator.maxLength(name, 50, "name") }
    }

    @Test
    fun `create lump sum - check if description length is validated`() {
        val lumpSum = ProgrammeLumpSum(
            id = 1L,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
        )
        val description = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(256)))
        every {
            generalValidator.maxLength(description, 255, "description")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            createLumpSum.createLumpSum(lumpSum.copy(description = description))
        }
        verify(exactly = 1) { generalValidator.maxLength(description, 255, "description") }

    }

    @Test
    fun `create lump sum - check if phase is validated`() {
        val lumpSum = ProgrammeLumpSum(
            id = 1L,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
        )
        val phase = null
        every {
            generalValidator.notNull(phase, "phase")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            createLumpSum.createLumpSum(lumpSum.copy(phase = phase))
        }
        verify(exactly = 1) { generalValidator.notNull(phase, "phase") }
    }

}
