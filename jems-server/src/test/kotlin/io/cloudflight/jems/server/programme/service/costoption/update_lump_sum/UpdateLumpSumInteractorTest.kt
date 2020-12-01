package io.cloudflight.jems.server.programme.service.costoption.update_lump_sum

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase.Implementation
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
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
import java.util.stream.Collectors

class UpdateLumpSumInteractorTest {

    @MockK
    lateinit var persistence: ProgrammeLumpSumPersistence

    @MockK
    lateinit var auditService: AuditService

    private lateinit var updateLumpSumInteractor: UpdateLumpSumInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        updateLumpSumInteractor = UpdateLumpSum(persistence, auditService)
    }

    @Test
    fun `update lump sum - invalid`() {
        val wrongLumpSum = ProgrammeLumpSum(
            id = 4,
            name = " ",
            description = "test lump sum 1",
            cost = null,
            splittingAllowed = true,
            phase = null,
            categories = setOf(OfficeAndAdministrationCosts),
        )
        val ex = assertThrows<I18nValidationException> { updateLumpSumInteractor.updateLumpSum(wrongLumpSum) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "name" to I18nFieldError(i18nKey = "programme.lumpSum.name.should.not.be.empty"),
            "cost" to I18nFieldError(i18nKey = "programme.lumpSum.cost.invalid"),
            "phase" to I18nFieldError(i18nKey = "programme.lumpSum.phase.invalid"),
            "categories" to I18nFieldError(i18nKey = "programme.lumpSum.categories.min.2"),
        ))
    }

    @Test
    fun `update lump sum - long strings`() {
        val wrongLumpSum = ProgrammeLumpSum(
            id = 4,
            name = getStringOfLength(51),
            description = getStringOfLength(501),
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
        every { persistence.updateLumpSum(any()) } returnsArgument 0
        val lumpSum = ProgrammeLumpSum(
            id = 4,
            name = "LS1",
            description = "test lump sum 1",
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
            description = "Programme lump sum (id=4) 'LS1' has been changed"
        ))
    }

    @Test
    fun `update lump sum - wrong ID filled in`() {
        val lumpSum = ProgrammeLumpSum(
            name = "LS1",
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
        )

        assertThrows<I18nValidationException>("when updating id cannot be invalid") {
            updateLumpSumInteractor.updateLumpSum(lumpSum.copy(id = 0)) }
        assertThrows<I18nValidationException>("when updating id cannot be invalid") {
            updateLumpSumInteractor.updateLumpSum(lumpSum.copy(id = null)) }
    }

    @Test
    fun `update lump sum - not existing`() {
        val lumpSum = ProgrammeLumpSum(
            id = 777,
            name = "LS1",
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = Implementation,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        every { persistence.updateLumpSum(any()) } throws ResourceNotFoundException("programmeLumpSum")

        assertThrows<ResourceNotFoundException>("when updating not existing lump sum") {
            updateLumpSumInteractor.updateLumpSum(lumpSum) }
    }

    private fun getStringOfLength(length: Int): String =
        IntArray(length).map { "x" }.stream().collect(Collectors.joining())

}
