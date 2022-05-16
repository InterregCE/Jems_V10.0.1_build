package io.cloudflight.jems.server.programme.service.costoption.create_unit_cost

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.*
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
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

class CreateUnitCostInteractorTest : UnitTest() {

    private val inputErrorMap = mapOf("error" to I18nMessage("error.key"))

    @MockK
    lateinit var persistence: ProgrammeUnitCostPersistence

    @MockK
    lateinit var auditService: AuditService

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    private lateinit var createUnitCost: CreateUnitCost

    @BeforeEach
    fun reset() {
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(
            inputErrorMap
        )
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
        val ex = assertThrows<AppInputValidationException> { createUnitCost.createUnitCost(wrongUnitCost) }
        assertThat(ex.formErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "costPerUnit" to I18nMessage(i18nKey = "programme.unitCost.costPerUnit.invalid", mapOf("costPerUnit" to wrongUnitCost.costPerUnit.toString())),
            "categories" to I18nMessage(i18nKey = "programme.unitCost.categories.min.2"),
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
        val ex = assertThrows<AppInputValidationException> { createUnitCost.createUnitCost(wrongUnitCost) }
        assertThat(ex.formErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "costPerUnit" to I18nMessage(i18nKey = "programme.unitCost.costPerUnit.invalid", mapOf("costPerUnit" to wrongUnitCost.costPerUnit.toString())),
            "categories" to I18nMessage(i18nKey = "programme.unitCost.categories.exactly.1"),
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
        val ex = assertThrows<AppInputValidationException> { createUnitCost.createUnitCost(wrongUnitCost) }
        assertThat(ex.formErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "categories" to I18nMessage(i18nKey = "programme.unitCost.categories.restricted"),
        ))
    }

    @Test
    fun `create unit cost - reached max allowed amount`() {
        every { persistence.getCount() } returns 100
        val unitCost = ProgrammeUnitCost(
            id = 0,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val ex = assertThrows<I18nValidationException> { createUnitCost.createUnitCost(unitCost) }
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

        assertThat(createUnitCost.createUnitCost(unitCost)).isEqualTo(unitCost.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_UNIT_COST_ADDED,
            description = "Programme unit cost (id=0) '[EN=UC1]' has been added" // null will be real ID from DB sequence
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

        assertThat(createUnitCost.createUnitCost(unitCost)).isEqualTo(unitCost.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_UNIT_COST_ADDED,
            description = "Programme unit cost (id=0) '[EN=UC1]' has been added" // null will be real ID from DB sequence
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
            createUnitCost.createUnitCost(unitCost) }
    }

    @Test
    fun `create unit cost - check if description length is validated`() {
        val unitCost = ProgrammeUnitCost(
            id = 0,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = true,
            categories = setOf(StaffCosts),
        )
        val description = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(256)))
        every {
            generalValidator.maxLength(description, 255, "description")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            createUnitCost.createUnitCost(unitCost.copy(description = description))
        }
        verify(exactly = 1) { generalValidator.maxLength(description, 255, "description") }
    }

    @Test
    fun `create unit cost - check if name length is validated`() {
        val unitCost = ProgrammeUnitCost(
            id = 0,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = true,
            categories = setOf(StaffCosts),
        )
        val name = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(51)))
        every {
            generalValidator.maxLength(name, 50, "name")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            createUnitCost.createUnitCost(unitCost.copy(name = name))
        }
        verify(exactly = 1) { generalValidator.maxLength(name, 50, "name") }
    }

    @Test
    fun `create unit cost - check if type length is validated`() {
        val unitCost = ProgrammeUnitCost(
            id = 0,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "type 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = true,
            categories = setOf(StaffCosts),
        )
        val type = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(51)))
        every {
            generalValidator.maxLength(type, 25, "type")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            createUnitCost.createUnitCost(unitCost.copy(type = type))
        }
        verify(exactly = 1) { generalValidator.maxLength(type, 25, "type") }
    }

}
