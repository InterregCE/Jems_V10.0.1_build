package io.cloudflight.jems.server.programme.service.costoption.update_unit_cost

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.UpdateUnitCostWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import java.math.BigDecimal
import java.util.stream.Collectors
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateUnitCostInteractorTest : UnitTest() {

    private val initialUnitCost = ProgrammeUnitCost(
        id = 4,
        name = setOf(InputTranslation(SystemLanguage.EN, " ")),
        description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
        type = emptySet(),
        costPerUnit = BigDecimal.ZERO,
        isOneCostCategory = false,
        categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
    )

    @MockK
    lateinit var persistence: ProgrammeUnitCostPersistence

    @RelaxedMockK
    lateinit var auditService: AuditService

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    private lateinit var updateUnitCostInteractor: UpdateUnitCostInteractor

    @BeforeEach
    fun setup() {
        clearMocks(auditService)
        updateUnitCostInteractor = UpdateUnitCost(persistence, auditService, generalValidator)
        every { persistence.getUnitCost(any()) } returns initialUnitCost
        every { persistence.isProgrammeSetupRestricted() } returns false
    }

    @Test
    fun `update unit cost - test if various invalid values will fail`() {
        val wrongUnitCost = ProgrammeUnitCost(
            id = 4,
            name = setOf(InputTranslation(SystemLanguage.EN, " ")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = emptySet(),
            costPerUnit = null,
            isOneCostCategory = false,
            categories = setOf(OfficeAndAdministrationCosts),
        )
        val ex = assertThrows<I18nValidationException> { updateUnitCostInteractor.updateUnitCost(wrongUnitCost) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "costPerUnit" to I18nFieldError(i18nKey = "programme.unitCost.costPerUnit.invalid"),
            "categories" to I18nFieldError(i18nKey = "programme.unitCost.categories.min.2"),
        ))
    }

    @Test
    fun `update unit cost - test if longer strings than allowed will fail`() {
        val wrongUnitCost = ProgrammeUnitCost(
            id = 4,
            name = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(51))),
            description = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(501))),
            type = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(26))),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = setOf(StaffCosts, OfficeAndAdministrationCosts),
        )
        val ex = assertThrows<I18nValidationException> { updateUnitCostInteractor.updateUnitCost(wrongUnitCost) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "name" to I18nFieldError(i18nKey = "programme.unitCost.name.too.long"),
            "description" to I18nFieldError(i18nKey = "programme.unitCost.description.too.long"),
            "type" to I18nFieldError(i18nKey = "programme.unitCost.type.too.long"),
        ))
    }

    @Test
    fun `update unit cost - test if valid UnitCost is properly saved`() {
        every { persistence.updateUnitCost(any()) } returnsArgument 0
        val unitCost = ProgrammeUnitCost(
            id = 4,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "test type 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val auditSlot = slot<AuditCandidate>()
        every { auditService.logEvent(capture(auditSlot)) } answers {}
        assertThat(updateUnitCostInteractor.updateUnitCost(unitCost)).isEqualTo(unitCost.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_UNIT_COST_CHANGED,
            description = "Programme unit cost (id=4) '[InputTranslation(language=EN, translation=UC1)]' has been changed"
        ))
    }

    @Test
    fun `update unit cost - test if validation will fail when wrong ID is filled in`() {
        val unitCost = ProgrammeUnitCost(
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
        )

        assertThrows<I18nValidationException>("when updating id cannot be invalid") {
            updateUnitCostInteractor.updateUnitCost(unitCost.copy(id = 0)) }
    }

    @Test
    fun `update unit cost - test if not existing UnitCost will fail with correct exception`() {
        val unitCost = ProgrammeUnitCost(
            id = 777,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "UC1 type")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        every { persistence.updateUnitCost(any()) } throws ResourceNotFoundException("programmeUnitCost")

        assertThrows<ResourceNotFoundException>("when updating not existing unit cost") {
            updateUnitCostInteractor.updateUnitCost(unitCost) }
    }

    @Test
    fun `update unit cost - call already published with same costPerUnit effective value but different number of decimal zeros`() {
        every { persistence.updateUnitCost(any()) } returnsArgument 0
        every { persistence.isProgrammeSetupRestricted() } returns true
        val unitCost = ProgrammeUnitCost(
            id = 4,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1 changed")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1 changed")),
            type = emptySet(),
            costPerUnit = BigDecimal(0),
            isOneCostCategory = false,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val auditSlot = slot<AuditCandidate>()
        every { auditService.logEvent(capture(auditSlot)) } answers {}
        assertThat(updateUnitCostInteractor.updateUnitCost(unitCost)).isEqualTo(unitCost.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_UNIT_COST_CHANGED,
            description = "Programme unit cost (id=4) '[InputTranslation(language=EN, translation=UC1 changed)]' has been changed"
        ))
    }

    @Test
    fun `update unit cost - call already published with different costPerUnit value`() {
        every { persistence.isProgrammeSetupRestricted() } returns true

        assertThrows<UpdateUnitCostWhenProgrammeSetupRestricted> {updateUnitCostInteractor.updateUnitCost(initialUnitCost.copy(costPerUnit = BigDecimal.TEN))}
    }
}
