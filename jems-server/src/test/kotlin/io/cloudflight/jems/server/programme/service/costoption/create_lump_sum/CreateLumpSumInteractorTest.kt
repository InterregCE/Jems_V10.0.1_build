package io.cloudflight.jems.server.programme.service.costoption.create_lump_sum

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase.Implementation
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class CreateLumpSumInteractorTest {

    @MockK
    lateinit var persistence: ProgrammeLumpSumPersistence

    @MockK
    lateinit var auditService: AuditService

    private lateinit var createLumpSumInteractor: CreateLumpSumInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        createLumpSumInteractor = CreateLumpSum(persistence, auditService)
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
        val ex = assertThrows<I18nValidationException> { createLumpSumInteractor.createLumpSum(wrongLumpSum) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "cost" to I18nFieldError(i18nKey = "lump.sum.out.of.range"),
            "phase" to I18nFieldError(i18nKey = "lump.sum.phase.should.not.be.empty"),
            "categories" to I18nFieldError(i18nKey = "programme.lumpSum.categories.min.2"),
        ))
    }

    @Test
    fun `create lump sum - reached max allowed amount`() {
        every { persistence.getCount() } returns 25
        val lumpSum = ProgrammeLumpSum(
            id = 0L,
            name = setOf(InputTranslation(SystemLanguage.EN, "LS1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test lump sum 1")),
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val ex = assertThrows<I18nValidationException> { createLumpSumInteractor.createLumpSum(lumpSum) }
        assertThat(ex.i18nKey).isEqualTo("programme.lumpSum.max.allowed.reached")
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

        assertThat(createLumpSumInteractor.createLumpSum(lumpSum)).isEqualTo(lumpSum.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_LUMP_SUM_ADDED,
            description = "Programme lump sum (id=0) '[InputTranslation(language=EN, translation=LS1)]' has been added" // null will be real ID from DB sequence
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

        assertThrows<I18nValidationException>("when creating id cannot be filled in") {
            createLumpSumInteractor.createLumpSum(lumpSum) }
    }

}
