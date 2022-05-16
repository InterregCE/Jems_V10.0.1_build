package io.cloudflight.jems.server.programme.service.costoption.update_unit_cost

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.UpdateUnitCostWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.info.hasProjectsInStatus.HasProjectsInStatusInteractor
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
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
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal

class UpdateUnitCostInteractorTest : UnitTest() {

    private val inputErrorMap = mapOf("error" to I18nMessage("error.key"))

    private val initialUnitCost = ProgrammeUnitCost(
        id = 4,
        name = setOf(InputTranslation(SystemLanguage.EN, " ")),
        description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
        type = emptySet(),
        costPerUnit = BigDecimal.ONE,
        isOneCostCategory = false,
        categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
    )

    @MockK
    lateinit var persistence: ProgrammeUnitCostPersistence

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @MockK
    lateinit var hasProjectsInStatus: HasProjectsInStatusInteractor

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    private lateinit var updateUnitCost: UpdateUnitCost

    @BeforeEach
    fun reset() {
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(
            inputErrorMap
        )
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
        val ex = assertThrows<AppInputValidationException> { updateUnitCost.updateUnitCost(wrongUnitCost) }
        assertThat(ex.formErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "costPerUnit" to I18nMessage(i18nKey = "programme.unitCost.costPerUnit.invalid", mapOf("costPerUnit" to wrongUnitCost.costPerUnit.toString())),
            "categories" to I18nMessage(i18nKey = "programme.unitCost.categories.min.2"),
        ))
    }

    @Test
    fun `update unit cost - test if valid UnitCost is properly saved`() {
        every { persistence.updateUnitCost(any()) } returnsArgument 0
        every { persistence.getUnitCost(any()) } returns initialUnitCost
        val unitCost = ProgrammeUnitCost(
            id = 4,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1")),
            type = setOf(InputTranslation(SystemLanguage.EN, "test type 1")),
            costPerUnit = BigDecimal.ONE,
            isOneCostCategory = false,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        assertThat(updateUnitCost.updateUnitCost(unitCost)).isEqualTo(unitCost.copy())
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_UNIT_COST_CHANGED,
            description = "Programme unit cost (id=4) '[EN=UC1]' has been changed"
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
            updateUnitCost.updateUnitCost(unitCost.copy(id = 0)) }
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
            updateUnitCost.updateUnitCost(unitCost) }
    }

    @Test
    fun `update unit cost - call already published with same costPerUnit effective value but different number of decimal zeros`() {
        every { persistence.updateUnitCost(any()) } returnsArgument 0
        every { isProgrammeSetupLocked.isLocked() } returns true
        every { hasProjectsInStatus.programmeHasProjectsInStatus(ApplicationStatusDTO.CONTRACTED) } returns false
        every { persistence.getUnitCost(any()) } returns initialUnitCost
        val unitCost = ProgrammeUnitCost(
            id = 4,
            name = setOf(InputTranslation(SystemLanguage.EN, "UC1 changed")),
            description = setOf(InputTranslation(SystemLanguage.EN, "test unit cost 1 changed")),
            type = emptySet(),
            costPerUnit = BigDecimal(1),
            isOneCostCategory = false,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        assertThat(updateUnitCost.updateUnitCost(unitCost)).isEqualTo(unitCost.copy())
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_UNIT_COST_CHANGED,
            description = "Programme unit cost (id=4) '[EN=UC1 changed]' has been changed"
        ))
    }

    @Test
    fun `update unit cost - call already published with different costPerUnit value`() {
        every { isProgrammeSetupLocked.isLocked() } returns true

        assertThrows<UpdateUnitCostWhenProgrammeSetupRestricted> {updateUnitCost.updateUnitCost(initialUnitCost.copy(costPerUnit = BigDecimal.TEN))}
    }

    @Test
    fun `update unit cost - check if description length is validated`() {
        val description = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(256)))
        every {
            generalValidator.maxLength(description, 255, "description")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updateUnitCost.updateUnitCost(initialUnitCost.copy(description = description))
        }
        verify(exactly = 1) { generalValidator.maxLength(description, 255, "description") }
    }

    @Test
    fun `update unit cost - check if name length is validated`() {
        val name = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(51)))
        every {
            generalValidator.maxLength(name, 50, "name")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updateUnitCost.updateUnitCost(initialUnitCost.copy(name = name))
        }
        verify(exactly = 1) { generalValidator.maxLength(name, 50, "name") }
    }

    @Test
    fun `update unit cost - check if type length is validated`() {
        val type = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(51)))
        every {
            generalValidator.maxLength(type, 25, "type")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updateUnitCost.updateUnitCost(initialUnitCost.copy(type = type))
        }
        verify(exactly = 1) { generalValidator.maxLength(type, 25, "type") }
    }
}
