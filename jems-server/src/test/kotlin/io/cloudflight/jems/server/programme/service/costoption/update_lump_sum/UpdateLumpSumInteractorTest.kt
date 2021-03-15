package io.cloudflight.jems.server.programme.service.costoption.update_lump_sum

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase.Implementation
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.UpdateLumpSumWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

internal class UpdateLumpSumInteractorTest : UnitTest() {

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

    @RelaxedMockK
    lateinit var auditService: AuditService

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    private lateinit var updateLumpSumInteractor: UpdateLumpSumInteractor

    @BeforeEach
    fun resetAuditService() {
        updateLumpSumInteractor = UpdateLumpSum(persistence, auditService, generalValidator)
        clearMocks(auditService)
    }

    @Test
    fun `update lump sum - invalid`() {
        every { persistence.isProgrammeSetupRestricted() } returns false
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
        val ex = assertThrows<I18nValidationException> { updateLumpSumInteractor.updateLumpSum(wrongLumpSum) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "cost" to I18nFieldError(i18nKey = "lump.sum.out.of.range"),
            "phase" to I18nFieldError(i18nKey = "lump.sum.phase.should.not.be.empty"),
            "categories" to I18nFieldError(i18nKey = "programme.lumpSum.categories.min.2"),
        ))
    }

    @Test
    fun `update lump sum - long strings`() {
        every { persistence.isProgrammeSetupRestricted() } returns false
        every { persistence.getLumpSum(any()) } returns initialLumpSum
        val wrongLumpSum = ProgrammeLumpSum(
            id = 4,
            name = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(51))),
            description = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(501))),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(StaffCosts, OfficeAndAdministrationCosts),
        )
        val ex = assertThrows<I18nValidationException> { updateLumpSumInteractor.updateLumpSum(wrongLumpSum) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "name" to I18nFieldError(i18nKey = "programme.lumpSum.name.too.long"),
            "description" to I18nFieldError(i18nKey = "programme.lumpSum.description.too.long"),
        ))
    }

    @Test
    fun `update lump sum - OK`() {
        every { persistence.isProgrammeSetupRestricted() } returns false
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
        assertThat(updateLumpSumInteractor.updateLumpSum(lumpSum)).isEqualTo(lumpSum.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_LUMP_SUM_CHANGED,
            description = "Programme lump sum (id=4) '[InputTranslation(language=EN, translation=LS1)]' has been changed"
        ))
    }

    @Test
    fun `update lump sum - wrong ID filled in`() {
        every { persistence.isProgrammeSetupRestricted() } returns false
        every { persistence.getLumpSum(any()) } returns initialLumpSum
        val lumpSum = ProgrammeLumpSum(
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
        )

        assertThrows<I18nValidationException>("when updating id cannot be invalid") {
            updateLumpSumInteractor.updateLumpSum(lumpSum.copy(id = 0)) }
    }

    @Test
    fun `update lump sum - not existing`() {
        every { persistence.isProgrammeSetupRestricted() } returns false
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
            updateLumpSumInteractor.updateLumpSum(lumpSum) }
    }

    @Test
    fun `update lump sum - call already published with same cost effective value but different number of decimal zeros`() {
        every { persistence.getLumpSum(any()) } returns initialLumpSum
        every { persistence.updateLumpSum(any()) } returnsArgument 0
        every { persistence.isProgrammeSetupRestricted() } returns true
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
        assertThat(updateLumpSumInteractor.updateLumpSum(lumpSum)).isEqualTo(lumpSum.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_LUMP_SUM_CHANGED,
            description = "Programme lump sum (id=4) '[InputTranslation(language=EN, translation=LS1 changed)]' has been changed"
        ))
    }

    @Test
    fun `update lump sum - call already published with different cost value`() {
        every { persistence.getLumpSum(any()) } returns initialLumpSum
        every { persistence.isProgrammeSetupRestricted() } returns true

        assertThrows<UpdateLumpSumWhenProgrammeSetupRestricted> {updateLumpSumInteractor.updateLumpSum(initialLumpSum.copy(cost = BigDecimal.TEN))}
    }
}
