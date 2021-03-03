package io.cloudflight.jems.server.programme.service.costoption.create_unit_cost

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.TravelAndAccommodationCosts
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class CreateUnitCostInteractorTest {

    @MockK
    lateinit var persistence: ProgrammeUnitCostPersistence

    @MockK
    lateinit var auditService: AuditService

    private lateinit var createUnitCostInteractor: CreateUnitCostInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        createUnitCostInteractor = CreateUnitCost(persistence, auditService)
    }

    @Test
    fun `create unit cost - invalid`() {
        every { persistence.getCount() } returns 15
        val wrongUnitCost = ProgrammeUnitCost(
            id = 0,
            name = setOf(InputTranslation(SystemLanguage.EN, " ")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "this type is longer than25")),
            costPerUnit = null,
            isOneCostCategory = false,
            categories = setOf(OfficeAndAdministrationCosts),
        )
        val ex = assertThrows<I18nValidationException> { createUnitCostInteractor.createUnitCost(wrongUnitCost) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "costPerUnit" to I18nFieldError(i18nKey = "programme.unitCost.costPerUnit.invalid"),
            "type" to I18nFieldError(i18nKey = "programme.unitCost.type.too.long"),
            "categories" to I18nFieldError(i18nKey = "programme.unitCost.categories.min.2"),
        ))
    }

    @Test
    fun `create unit cost - one cost category invalid`() {
        every { persistence.getCount() } returns 15
        val wrongUnitCost = ProgrammeUnitCost(
            id = 0,
            name = setOf(InputTranslation(SystemLanguage.EN, " ")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "this type is longer than25")),
            costPerUnit = null,
            isOneCostCategory = true,
            categories = setOf(StaffCosts, TravelAndAccommodationCosts),
        )
        val ex = assertThrows<I18nValidationException> { createUnitCostInteractor.createUnitCost(wrongUnitCost) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "costPerUnit" to I18nFieldError(i18nKey = "programme.unitCost.costPerUnit.invalid"),
            "type" to I18nFieldError(i18nKey = "programme.unitCost.type.too.long"),
            "categories" to I18nFieldError(i18nKey = "programme.unitCost.categories.exactly.1"),
        ))
    }

    @Test
    fun `create unit cost - one cost category OfficeAndAdministrationCosts restricted`() {
        every { persistence.getCount() } returns 15
        val wrongUnitCost = ProgrammeUnitCost(
            id = 0,
            name = setOf(InputTranslation(SystemLanguage.EN, "test")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "test type")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = true,
            categories = setOf(OfficeAndAdministrationCosts),
        )
        val ex = assertThrows<I18nValidationException> { createUnitCostInteractor.createUnitCost(wrongUnitCost) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "categories" to I18nFieldError(i18nKey = "programme.unitCost.categories.restricted"),
        ))
    }

    @Test
    fun `create unit cost - reached max allowed amount`() {
        every { persistence.getCount() } returns 25
        val unitCost = ProgrammeUnitCost(
            id = 0,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val ex = assertThrows<I18nValidationException> { createUnitCostInteractor.createUnitCost(unitCost) }
        assertThat(ex.i18nKey).isEqualTo("programme.unitCost.max.allowed.reached")
    }

    @Test
    fun `create unit cost - OK`() {
        every { persistence.getCount() } returns 5
        every { persistence.createUnitCost(any()) } returnsArgument 0
        val unitCost = ProgrammeUnitCost(
            id = 0,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val auditSlot = slot<AuditCandidate>()
        every { auditService.logEvent(capture(auditSlot)) } answers {}

        assertThat(createUnitCostInteractor.createUnitCost(unitCost)).isEqualTo(unitCost.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_UNIT_COST_ADDED,
            description = "Programme unit cost (id=0) '[InputTranslation(language=EN, translation=UC1)]' has been added" // null will be real ID from DB sequence
        ))
    }

    @Test
    fun `create unit cost - one cost category OK`() {
        every { persistence.getCount() } returns 5
        every { persistence.createUnitCost(any()) } returnsArgument 0
        val unitCost = ProgrammeUnitCost(
            id = 0,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = true,
            categories = setOf(StaffCosts),
        )
        val auditSlot = slot<AuditCandidate>()
        every { auditService.logEvent(capture(auditSlot)) } answers {}

        assertThat(createUnitCostInteractor.createUnitCost(unitCost)).isEqualTo(unitCost.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_UNIT_COST_ADDED,
            description = "Programme unit cost (id=0) '[InputTranslation(language=EN, translation=UC1)]' has been added" // null will be real ID from DB sequence
        ))
    }

    @Test
    fun `create unit cost - wrong ID filled in`() {
        every { persistence.getCount() } returns 10
        val unitCost = ProgrammeUnitCost(
            id = 1L,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "UC1 type")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false
        )

        assertThrows<I18nValidationException>("when creating id cannot be filled in") {
            createUnitCostInteractor.createUnitCost(unitCost) }
    }

}
