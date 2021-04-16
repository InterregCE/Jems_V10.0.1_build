package io.cloudflight.jems.server.programme.service.costoption.update_lump_sum

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase.Implementation
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.UpdateLumpSumWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
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

internal class UpdateLumpSumInteractorTest : UnitTest() {

    private val inputErrorMap = mapOf("error" to I18nMessage("error.key"))

    val initialLumpSum = ProgrammeLumpSum(
        id = 4,
        name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
        description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1")),
        cost = BigDecimal.ONE,
        splittingAllowed = true,
        phase = Implementation,
        categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
    )

    @MockK
    lateinit var persistence: ProgrammeLumpSumPersistence

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @RelaxedMockK
    lateinit var auditService: AuditService

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updateLumpSum: UpdateLumpSum

    @BeforeEach
    fun reset() {
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(
            inputErrorMap
        )
    }

    @Test
    fun `update lump sum - invalid`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getLumpSum(any()) } returns initialLumpSum
        val wrongLumpSum = ProgrammeLumpSum(
            id = 4,
            name = setOf(InputTranslation(SystemLanguage.EN, " ")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1")),
            cost = null,
            splittingAllowed = true,
            phase = null,
            categories = setOf(OfficeAndAdministrationCosts),
        )
        val ex = assertThrows<I18nValidationException> { updateLumpSum.updateLumpSum(wrongLumpSum) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "cost" to I18nFieldError(i18nKey = "lump.sum.out.of.range"),
            "categories" to I18nFieldError(i18nKey = "programme.lumpSum.categories.min.2"),
        ))
    }

    @Test
    fun `update lump sum - check if name length is validated`() {
        val name = setOf(InputTranslation(SystemLanguage.SK, getStringOfLength(51)))
        every {
            generalValidator.maxLength(name, 50, "name")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updateLumpSum.updateLumpSum(initialLumpSum.copy(name = name))
        }
        verify(exactly = 1) { generalValidator.maxLength(name, 50, "name") }
    }

    @Test
    fun `update lump sum - check if description length is validated`() {
        val description = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(256)))
        every {
            generalValidator.maxLength(description, 255, "description")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updateLumpSum.updateLumpSum(initialLumpSum.copy(description = description))
        }
        verify(exactly = 1) { generalValidator.maxLength(description, 255, "description") }
    }

    @Test
    fun `update lump sum - check if phase is validated`() {
        val phase = null
        every {
            generalValidator.notNull(phase, "phase")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updateLumpSum.updateLumpSum(initialLumpSum.copy(phase = phase))
        }
        verify(exactly = 1) { generalValidator.notNull(phase, "phase") }
    }

    @Test
    fun `update lump sum - OK`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getLumpSum(any()) } returns initialLumpSum
        every { persistence.updateLumpSum(any()) } returnsArgument 0
        val lumpSum = ProgrammeLumpSum(
            id = 4,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val auditSlot = slot<AuditCandidate>()
        every { auditService.logEvent(capture(auditSlot)) } answers {}
        assertThat(updateLumpSum.updateLumpSum(lumpSum)).isEqualTo(lumpSum.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_LUMP_SUM_CHANGED,
            description = "Programme lump sum (id=4) '[InputTranslation(language=EN, translation=LS1)]' has been changed"
        ))
    }

    @Test
    fun `update lump sum - wrong ID filled in`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getLumpSum(any()) } returns initialLumpSum
        val lumpSum = ProgrammeLumpSum(
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
        )

        assertThrows<I18nValidationException>("when updating id cannot be invalid") {
            updateLumpSum.updateLumpSum(lumpSum.copy(id = 0)) }
    }

    @Test
    fun `update lump sum - not existing`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getLumpSum(any()) } returns initialLumpSum
        val lumpSum = ProgrammeLumpSum(
            id = 777,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        every { persistence.updateLumpSum(any()) } throws ResourceNotFoundException("programmeLumpSum")

        assertThrows<ResourceNotFoundException>("when updating not existing lump sum") {
            updateLumpSum.updateLumpSum(lumpSum) }
    }

    @Test
    fun `update lump sum - call already published with same cost effective value but different number of decimal zeros`() {
        every { persistence.getLumpSum(any()) } returns initialLumpSum
        every { persistence.updateLumpSum(any()) } returnsArgument 0
        every { isProgrammeSetupLocked.isLocked() } returns true
        val lumpSum = ProgrammeLumpSum(
            id = 4,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1 changed")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1 changed")),
            cost = BigDecimal(1),
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val auditSlot = slot<AuditCandidate>()
        every { auditService.logEvent(capture(auditSlot)) } answers {}
        assertThat(updateLumpSum.updateLumpSum(lumpSum)).isEqualTo(lumpSum.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_LUMP_SUM_CHANGED,
            description = "Programme lump sum (id=4) '[InputTranslation(language=EN, translation=LS1 changed)]' has been changed"
        ))
    }

    @Test
    fun `update lump sum - call already published with different cost value`() {
        every { persistence.getLumpSum(any()) } returns initialLumpSum
        every { isProgrammeSetupLocked.isLocked() } returns true

        assertThrows<UpdateLumpSumWhenProgrammeSetupRestricted> {updateLumpSum.updateLumpSum(initialLumpSum.copy(cost = BigDecimal.TEN))}
    }
}
