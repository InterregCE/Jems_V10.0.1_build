package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.AllowedRealCosts
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.CallApplicationFormFieldsConfiguration
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.stream.IntStream
import kotlin.streams.toList

internal class BudgetCostValidatorTest : UnitTest() {

    companion object {
        private const val callId = 2L
    }

    @RelaxedMockK
    lateinit var callPersistence: CallPersistence

    @InjectMockKs
    lateinit var budgetCostValidator: BudgetCostValidator

    @Test
    fun `should throw I18nValidationException when number of entries is more that allowed`() {
        val budgetCostEntries = IntStream.range(0, MAX_ALLOWED_NUMBER_OF_BUDGET_ENTRIES.plus(1)).toList().map {
            object : BaseBudgetEntry {
                override val id = it.toLong()
                override val numberOfUnits = BigDecimal.ZERO
                override val budgetPeriods = mutableSetOf<BudgetPeriod>()
                override val rowSum = BigDecimal.ZERO
                override val unitCostId = null
            }
        }
        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validateBaseEntries(budgetCostEntries)
        }

        assertEquals(BUDGET_COST_MAX_NUMBER_OF_ENTRIES_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException when at least to entries have the same id`() {
        val budgetCostEntries = listOf(
            object : BaseBudgetEntry {
                override val id: Long? = null
                override val numberOfUnits = BigDecimal.ZERO
                override val budgetPeriods = mutableSetOf<BudgetPeriod>()
                override val rowSum = BigDecimal.ZERO
                override val unitCostId = null
            },
            object : BaseBudgetEntry {
                override val id = 1L
                override val numberOfUnits = BigDecimal.ZERO
                override val budgetPeriods = mutableSetOf<BudgetPeriod>()
                override val rowSum = BigDecimal.ZERO
                override val unitCostId = null
            },
            object : BaseBudgetEntry {
                override val id = 1L
                override val numberOfUnits = BigDecimal.ZERO
                override val budgetPeriods = mutableSetOf<BudgetPeriod>()
                override val rowSum = BigDecimal.ZERO
                override val unitCostId = null
            }
        )
        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validateBaseEntries(budgetCostEntries)
        }

        assertEquals(BUDGET_COST_DUPLICATE_ENTRY_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should not throw Exception when there are multiple entries with id equal to null`() {
        val budgetCostEntries = listOf(
            object : BaseBudgetEntry {
                override val id: Long? = null
                override val numberOfUnits = BigDecimal.ZERO
                override val budgetPeriods = mutableSetOf<BudgetPeriod>()
                override val rowSum = BigDecimal.ZERO
                override val unitCostId = null
            },
            object : BaseBudgetEntry {
                override val id: Long? = null
                override val numberOfUnits = BigDecimal.ZERO
                override val budgetPeriods = mutableSetOf<BudgetPeriod>()
                override val rowSum = BigDecimal.ZERO
                override val unitCostId = null
            },
        )
        val result = budgetCostValidator.validateBaseEntries(budgetCostEntries)

        assertEquals(Unit, result)

    }

    @Test
    fun `should throw I18nValidationException when at least amount of one period is more than allowed`() {
        val invalidPeriods = mutableSetOf(BudgetPeriod(1, MAX_ALLOWED_BUDGET_VALUE.plus(BigDecimal.ONE).truncate()))

        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validateBudgetPeriods(invalidPeriods, setOf(1))
        }
        assertEquals(BUDGET_COST_VALUE_LIMIT_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException when at least scale of amount of one period is more that 2`() {
        val invalidPeriods = mutableSetOf(BudgetPeriod(1, BigDecimal.valueOf(22.345)))

        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validateBudgetPeriods(invalidPeriods, setOf(1))
        }
        assertEquals(BUDGET_COST_INVALID_PERIOD_AMOUNT_SCALE_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException when at least one period number is invalid`() {
        val invalidPeriods = mutableSetOf(BudgetPeriod(1, BigDecimal.TEN))
        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validateBudgetPeriods(invalidPeriods, setOf(2))
        }
        assertEquals(BUDGET_COST_PERIOD_NOT_EXISTS_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException when at least one pricePerUnit value is more than allowed`() {
        val pricePerUnits = listOf(BigDecimal.ONE, BigDecimal.TEN, MAX_ALLOWED_BUDGET_VALUE.plus(BigDecimal.ONE))
        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validatePricePerUnits(pricePerUnits)
        }
        assertEquals(BUDGET_COST_VALUE_LIMIT_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException when at least one pricePerUnit scale is more that 2`() {
        val pricePerUnits = listOf(BigDecimal.ONE, BigDecimal.TEN, BigDecimal.valueOf(34.212))
        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validatePricePerUnits(pricePerUnits)
        }
        assertEquals(BUDGET_COST_INVALID_PRICE_PER_UNIT_SCALE_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should not throw exception if spf costs are added for spf call`() {
        assertEquals(budgetCostValidator.validateAllowedSpfCosts(createCallSettings(CallType.SPF)), Unit)
    }

    @Test
    fun `should throw I18nValidationException when at spf costs are added for standard call`() {
        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validateAllowedSpfCosts(createCallSettings(CallType.STANDARD))
        }
        assertEquals(BUDGET_COST_SPF_COST_NOT_ALLOWED, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException when numberOfUnits not acc to AF config`() {
        val numberOfUnits = listOf(BigDecimal.TEN)
        val afConfiguration = CallApplicationFormFieldsConfiguration(
            CallType.STANDARD,
            mutableSetOf(
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_BUDGET_EQUIPMENT_UNIT_TYPE_AND_NUMBER_OF_UNITS.id,
                    FieldVisibilityStatus.NONE
                )
            )
        )
        every { callPersistence.getApplicationFormFieldConfigurations(callId) } returns afConfiguration

        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validateAgainstAFConfig(
                callId,
                emptySet(),
                BudgetCategory.EquipmentCosts,
                numberOfUnits,
                emptyList()
            )
        }
        assertEquals(BUDGET_COST_NUMBER_UNITS_NOT_ENABLED_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException when periods not acc to AF config`() {
        val periods = setOf(BudgetPeriod(1, BigDecimal.ONE))
        val afConfiguration = CallApplicationFormFieldsConfiguration(
            CallType.STANDARD,
            mutableSetOf(
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_BUDGET_PERIODS.id,
                    FieldVisibilityStatus.NONE
                )
            )
        )
        every { callPersistence.getApplicationFormFieldConfigurations(callId) } returns afConfiguration

        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validateAgainstAFConfig(
                callId,
                periods,
                BudgetCategory.StaffCosts,
                listOf(BigDecimal.ONE),
                emptyList()
            )
        }
        assertEquals(BUDGET_COST_PERIODS_NOT_ENABLED_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException when unitType not acc to AF config`() {
        val unitTypes = listOf(Pair(null, setOf(InputTranslation(SystemLanguage.EN, "string"))))
        val afConfiguration = CallApplicationFormFieldsConfiguration(
            CallType.STANDARD,
            mutableSetOf(
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_UNIT_TYPE_AND_NUMBER_OF_UNITS.id,
                    FieldVisibilityStatus.NONE
                )
            )
        )
        every { callPersistence.getApplicationFormFieldConfigurations(callId) } returns afConfiguration

        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validateAgainstAFConfig(
                callId,
                emptySet(),
                BudgetCategory.InfrastructureCosts,
                listOf(BigDecimal.ONE),
                unitTypes
            )
        }
        assertEquals(BUDGET_COST_NUMBER_UNITS_NOT_ENABLED_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should be successful when data acc to AF config`() {
        val periods = setOf(BudgetPeriod(1, BigDecimal.ONE))
        val numberOfUnits = listOf(BigDecimal.TEN)
        val unitTypes = listOf(Pair(null, setOf(InputTranslation(SystemLanguage.EN, "string"))))
        val afConfiguration = CallApplicationFormFieldsConfiguration(
            CallType.STANDARD,
            mutableSetOf(
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_BUDGET_PERIODS.id,
                    FieldVisibilityStatus.STEP_TWO_ONLY
                ),
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_BUDGET_EXTERNAL_EXPERTISE_UNIT_TYPE_AND_NUMBER_OF_UNITS.id,
                    FieldVisibilityStatus.STEP_TWO_ONLY
                )
            )
        )
        every { callPersistence.getApplicationFormFieldConfigurations(callId) } returns afConfiguration

        assertDoesNotThrow { budgetCostValidator.validateAgainstAFConfig(
                callId,
                periods,
                BudgetCategory.ExternalCosts,
                numberOfUnits,
                unitTypes
            )
        }
    }

    @Test
    fun `should throw I18nValidationException when SPF data not acc to AF config`() {
        val periods = setOf(BudgetPeriod(1, BigDecimal.ONE))
        val numberOfUnits = listOf(BigDecimal.ONE)
        val unitTypes = listOf(Pair(null, setOf(InputTranslation(SystemLanguage.EN, "string"))))
        val afConfiguration = CallApplicationFormFieldsConfiguration(
            CallType.SPF,
            mutableSetOf(
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_BUDGET_PERIODS.id,
                    FieldVisibilityStatus.STEP_TWO_ONLY
                ),
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PARTNER_BUDGET_SPF_UNIT_TYPE_AND_NUMBER_OF_UNITS.id,
                    FieldVisibilityStatus.NONE
                )
            )
        )
        every { callPersistence.getApplicationFormFieldConfigurations(callId) } returns afConfiguration

        val ex = assertThrows<I18nValidationException> { budgetCostValidator.validateAgainstAFConfig(
            callId,
            periods,
            null,
            numberOfUnits,
            unitTypes
        )}
        assertEquals(BUDGET_COST_NUMBER_UNITS_NOT_ENABLED_ERROR_KEY, ex.i18nKey)
    }

    @Test
    fun `should throw I18nValidationException when Staff real costs not allowed`() {
        val budgetCostEntries = listOf(
            object : BaseBudgetEntry {
                override val id: Long? = null
                override val numberOfUnits = BigDecimal.ZERO
                override val budgetPeriods = mutableSetOf<BudgetPeriod>()
                override val rowSum = BigDecimal.ZERO
                override val unitCostId = null
            }
        )
        val allowedRealCost = AllowedRealCosts(
            allowRealStaffCosts = false,
            allowRealTravelAndAccommodationCosts = false,
            allowRealExternalExpertiseAndServicesCosts = false,
            allowRealEquipmentCosts = false,
            allowRealInfrastructureCosts = false
        )
        every { callPersistence.getAllowedRealCosts(callId) } returns allowedRealCost

        val ex = assertThrows<I18nValidationException> {
            budgetCostValidator.validateAllowedRealCosts(callId, budgetCostEntries, BudgetCategory.StaffCosts)
        }
        assertEquals(BUDGET_COST_REAL_COST_NOT_ALLOWED, ex.i18nKey)
    }

    @Test
    fun `should be successful when Equipment real costs allowed`() {
        val budgetCostEntries = listOf(
            object : BaseBudgetEntry {
                override val id: Long? = null
                override val numberOfUnits = BigDecimal.ZERO
                override val budgetPeriods = mutableSetOf<BudgetPeriod>()
                override val rowSum = BigDecimal.ZERO
                override val unitCostId = null
            }
        )
        val allowedRealCost = AllowedRealCosts(
            allowRealStaffCosts = false,
            allowRealTravelAndAccommodationCosts = false,
            allowRealExternalExpertiseAndServicesCosts = false,
            allowRealEquipmentCosts = true,
            allowRealInfrastructureCosts = false
        )
        every { callPersistence.getAllowedRealCosts(callId) } returns allowedRealCost

        assertDoesNotThrow {
            budgetCostValidator.validateAllowedRealCosts(callId, budgetCostEntries, BudgetCategory.EquipmentCosts)
        }
    }

    @TestFactory
    fun `should throw I18nValidationException when at least one of number of units is more than allowed`() =
        listOf(
            createBaseBudgetEntries(withInvalidNumberOfUnits = true) to "numberOfUnits is more than allowed ",
            createBaseBudgetEntries(withInvalidNumberOfUnits = true) to "period amount is more than allowed ",
        ).map { (budgetEntries, argument) ->
            DynamicTest.dynamicTest(
                "should throw I18nValidationException when at least in one budget cost entry $argument"
            ) {
                val ex = assertThrows<I18nValidationException> {
                    budgetCostValidator.validateBaseEntries(budgetEntries)
                }
                assertEquals(BUDGET_COST_VALUE_LIMIT_ERROR_KEY, ex.i18nKey)
            }
        }

    private fun createBaseBudgetEntries(
        withInvalidNumberOfUnits: Boolean = false,
        withInvalidPeriodAmount: Boolean = false
    ): List<BaseBudgetEntry> =
        IntStream.range(1, 10).toList().map {
            object : BaseBudgetEntry {
                override val id = it.toLong()
                override val numberOfUnits = BigDecimal.ZERO.truncate()
                override val budgetPeriods = mutableSetOf<BudgetPeriod>()
                override val rowSum = BigDecimal.ZERO.truncate()
                override val unitCostId = null
            }
        }.plus(
            object : BaseBudgetEntry {
                override val id = 20L
                override val numberOfUnits = if (withInvalidNumberOfUnits) MAX_ALLOWED_BUDGET_VALUE.plus(BigDecimal.ONE)
                    .truncate() else BigDecimal.ZERO.truncate()
                override val budgetPeriods = if (withInvalidPeriodAmount) mutableSetOf(
                    BudgetPeriod(
                        1,
                        MAX_ALLOWED_BUDGET_VALUE.plus(BigDecimal.ONE).truncate()
                    )
                ) else mutableSetOf()
                override val rowSum = BigDecimal.ZERO.truncate()
                override val unitCostId = null
            }
        )

    private fun createCallSettings(callType: CallType): ProjectCallSettings {
        return ProjectCallSettings(
            callId = 1,
            callName = "callName",
            callType = callType,
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            endDateStep1 = null,
            lengthOfPeriod = 2,
            isAdditionalFundAllowed = false,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
            stateAids = emptyList(),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null
        )
    }
}
