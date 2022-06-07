package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.EquipmentCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.ExternalCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.InfrastructureCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.TravelAndAccommodationCosts
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.AllowedRealCosts
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting.PARTNER_BUDGET_EQUIPMENT_UNIT_TYPE_AND_NUMBER_OF_UNITS
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting.PARTNER_BUDGET_EXTERNAL_EXPERTISE_UNIT_TYPE_AND_NUMBER_OF_UNITS
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting.PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_UNIT_TYPE_AND_NUMBER_OF_UNITS
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting.PARTNER_BUDGET_SPF_UNIT_TYPE_AND_NUMBER_OF_UNITS
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting.PARTNER_BUDGET_STAFF_COST_UNIT_TYPE_AND_NUMBER_OF_UNITS
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting.PARTNER_BUDGET_TRAVEL_AND_ACCOMMODATION_UNIT_TYPE_AND_NUMBER_OF_UNITS
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.math.BigDecimal

const val MAX_ALLOWED_NUMBER_OF_BUDGET_ENTRIES = 300
const val BUDGET_COST_MAX_NUMBER_OF_ENTRIES_ERROR_KEY = "project.partner.budget.max.allowed.reached"
const val BUDGET_COST_DUPLICATE_ENTRY_ERROR_KEY = "project.partner.budget.duplicate.entry.error"
const val BUDGET_COST_VALUE_LIMIT_ERROR_KEY = "project.partner.budget.number.out.of.range"
const val BUDGET_COST_PERIOD_NOT_EXISTS_ERROR_KEY = "project.partner.budget.period.not.exist"
const val BUDGET_COST_PERIODS_NOT_ENABLED_ERROR_KEY = "project.partner.budget.period.not.enabled"
const val BUDGET_COST_NUMBER_UNITS_NOT_ENABLED_ERROR_KEY = "project.partner.budget.number.of.units.not.enabled"
const val BUDGET_COST_INVALID_PERIOD_AMOUNT_SCALE_ERROR_KEY = "project.partner.budget.period.amount.invalid.scale"
const val BUDGET_COST_INVALID_NUMBER_OF_UNITS_SCALE_ERROR_KEY = "project.partner.budget.number.of.units.invalid.scale"
const val BUDGET_COST_INVALID_PRICE_PER_UNIT_SCALE_ERROR_KEY = "project.partner.budget.price.per.unit.invalid.scale"
const val BUDGET_COST_REAL_COST_NOT_ALLOWED = "project.partner.budget.real.cost.not.allowed"
const val BUDGET_COST_SPF_COST_NOT_ALLOWED = "project.partner.budget.spf.cost.not.allowed"
val MAX_ALLOWED_BUDGET_VALUE: BigDecimal = BigDecimal.valueOf(999_999_999_99L, 2)

@Service
class BudgetCostValidator(private val callPersistence: CallPersistence) {

    final fun validateBaseEntries(budgetEntries: List<BaseBudgetEntry>) {

        if (budgetEntries.size > MAX_ALLOWED_NUMBER_OF_BUDGET_ENTRIES)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_MAX_NUMBER_OF_ENTRIES_ERROR_KEY
            )

        if (budgetEntries.filter { it.id != null }.size != budgetEntries.filter { it.id != null }
                .distinctBy { it.id }.size)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_DUPLICATE_ENTRY_ERROR_KEY
            )

        budgetEntries.forEach {
            if (it.numberOfUnits.scale() > 2)
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_INVALID_NUMBER_OF_UNITS_SCALE_ERROR_KEY
                )

            if (it.numberOfUnits > MAX_ALLOWED_BUDGET_VALUE)
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_VALUE_LIMIT_ERROR_KEY
                )
        }

    }


    fun validateBudgetPeriods(
        periods: Set<BudgetPeriod>,
        validPeriodNumbers: Set<Int>
    ) =
        periods.forEach { period ->
            if (!validPeriodNumbers.contains(period.number))
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_PERIOD_NOT_EXISTS_ERROR_KEY
                )
            if (period.amount.scale() > 2)
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_INVALID_PERIOD_AMOUNT_SCALE_ERROR_KEY
                )
            if (period.amount > MAX_ALLOWED_BUDGET_VALUE)
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_VALUE_LIMIT_ERROR_KEY
                )
        }

    fun validateAgainstAFConfig(
        callId: Long,
        periods: Set<BudgetPeriod>,
        budgetCategory: BudgetCategory?,
        numberOfUnits: List<BigDecimal>,
        unitTypes: List<Pair<Long?, Set<InputTranslation>>>
    ) {
        val afConfig = callPersistence.getApplicationFormFieldConfigurations(callId)
        val periodsOnBudget = afConfig.applicationFormFieldConfigurations
            .find { it.id == ApplicationFormFieldSetting.PARTNER_BUDGET_PERIODS.id }
        if (periods.isNotEmpty() && periodsOnBudget?.visibilityStatus == FieldVisibilityStatus.NONE )
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_PERIODS_NOT_ENABLED_ERROR_KEY
            )

        val fieldNumberOfUnits = getFieldNumberOfUnitsForCategory(budgetCategory)
        val numberOfUnitsAndUnitTypesDisabled = afConfig.applicationFormFieldConfigurations
            .find { it.id == fieldNumberOfUnits?.id }?.visibilityStatus == FieldVisibilityStatus.NONE
        if (numberOfUnitsAndUnitTypesDisabled
            && (numberOfUnits.any { it != BigDecimal.ONE } || isUnitTypeSetForRealCosts(unitTypes))) {
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_NUMBER_UNITS_NOT_ENABLED_ERROR_KEY
            )
        }
    }

    private fun isUnitTypeSetForRealCosts(unitTypes: List<Pair<Long?, Set<InputTranslation>>>) =
        unitTypes.any { it.second.isNotEmpty() && it.first == null }

    private fun getFieldNumberOfUnitsForCategory(budgetCategory: BudgetCategory?): ApplicationFormFieldSetting? {
        return when (budgetCategory) {
            StaffCosts -> PARTNER_BUDGET_STAFF_COST_UNIT_TYPE_AND_NUMBER_OF_UNITS
            TravelAndAccommodationCosts -> PARTNER_BUDGET_TRAVEL_AND_ACCOMMODATION_UNIT_TYPE_AND_NUMBER_OF_UNITS
            ExternalCosts -> PARTNER_BUDGET_EXTERNAL_EXPERTISE_UNIT_TYPE_AND_NUMBER_OF_UNITS
            EquipmentCosts -> PARTNER_BUDGET_EQUIPMENT_UNIT_TYPE_AND_NUMBER_OF_UNITS
            InfrastructureCosts -> PARTNER_BUDGET_INFRASTRUCTURE_AND_WORKS_UNIT_TYPE_AND_NUMBER_OF_UNITS
            null -> PARTNER_BUDGET_SPF_UNIT_TYPE_AND_NUMBER_OF_UNITS
            else -> null
        }
    }

    fun validatePricePerUnits(pricePerUnits: List<BigDecimal>) =
        pricePerUnits.stream().forEach { pricePerUnit ->

            if (pricePerUnit > MAX_ALLOWED_BUDGET_VALUE)
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_VALUE_LIMIT_ERROR_KEY
                )

            if (pricePerUnit.scale() > 2)
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_INVALID_PRICE_PER_UNIT_SCALE_ERROR_KEY
                )
        }

    fun validateAllowedSpfCosts(callSettings: ProjectCallSettings) {
        if (callSettings.callType != CallType.SPF) {
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_SPF_COST_NOT_ALLOWED
            )
        }
    }

    fun validateAllowedRealCosts(callId: Long, budgetEntries: List<BaseBudgetEntry>, budgetCategory: BudgetCategory) {
        val allowedRealCosts = callPersistence.getAllowedRealCosts(callId)

        if (realCostNotAllowed(allowedRealCosts, budgetCategory)) {
            validateAllEntriesAreUnitCosts(budgetEntries)
        }
    }

    private fun realCostNotAllowed(allowedRealCosts: AllowedRealCosts, budgetCategory: BudgetCategory): Boolean {
        return (budgetCategory == StaffCosts && !allowedRealCosts.allowRealStaffCosts)
            || (budgetCategory == TravelAndAccommodationCosts && !allowedRealCosts.allowRealTravelAndAccommodationCosts)
            || (budgetCategory == ExternalCosts && !allowedRealCosts.allowRealExternalExpertiseAndServicesCosts)
            || (budgetCategory == EquipmentCosts && !allowedRealCosts.allowRealEquipmentCosts)
            || (budgetCategory == InfrastructureCosts && !allowedRealCosts.allowRealInfrastructureCosts)
    }

    private fun validateAllEntriesAreUnitCosts(budgetEntries: List<BaseBudgetEntry>) {
        if (budgetEntries.any { it.unitCostId == null }) {
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_REAL_COST_NOT_ALLOWED
            )
        }
    }
}
