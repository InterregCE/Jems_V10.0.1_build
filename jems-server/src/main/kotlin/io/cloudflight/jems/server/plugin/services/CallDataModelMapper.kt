package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.plugin.contract.models.call.ApplicationFormFieldConfigurationData
import io.cloudflight.jems.plugin.contract.models.call.CallDetailData
import io.cloudflight.jems.plugin.contract.models.call.CallStatusData
import io.cloudflight.jems.plugin.contract.models.call.FieldVisibilityStatusData
import io.cloudflight.jems.plugin.contract.models.call.flatrate.FlatRateData
import io.cloudflight.jems.plugin.contract.models.call.flatrate.FlatRateSetupData
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.plugin.contract.models.programme.lumpsum.ProgrammeLumpSumListData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammeObjectiveData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammeObjectivePolicyData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammePriorityData
import io.cloudflight.jems.plugin.contract.models.programme.priority.ProgrammeSpecificObjectiveData
import io.cloudflight.jems.plugin.contract.models.programme.strategy.ProgrammeStrategyData
import io.cloudflight.jems.plugin.contract.models.programme.unitcost.BudgetCategoryData
import io.cloudflight.jems.plugin.contract.models.programme.unitcost.ProgrammeUnitCostListData
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective

fun CallDetail.toDataModel() = CallDetailData(
    id = id,
    name = name,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    status = CallStatusData.valueOf(status.name),
    startDateTime = startDate,
    endDateTimeStep1 = endDateStep1,
    endDateTime = endDate,
    lengthOfPeriod = lengthOfPeriod,
    description = description.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
    objectives = objectives.map { it.toDataModel() },
    strategies = strategies.map { ProgrammeStrategyData.valueOf(it.name)}.sorted(),
    funds = funds.toProgrammeFundDataModel(),
    flatRates = flatRates.toDataModel(),
    lumpSums = lumpSums.toLumpSumDataModel(),
    unitCosts = unitCosts.toUnitCostDataModel(),
    applicationFormFieldConfigurations = applicationFormFieldConfigurations.toDataModel()
)

fun ProgrammePriority.toDataModel() = ProgrammePriorityData(
    id = id,
    code = code,
    title = title.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
    objective = ProgrammeObjectiveData.valueOf(objective.name),
    specificObjectives = specificObjectives.map { it.toDataModel() }
)

fun ProgrammeSpecificObjective.toDataModel() = ProgrammeSpecificObjectiveData(
    code = code,
    programmeObjectivePolicy = ProgrammeObjectivePolicyData.valueOf(programmeObjectivePolicy.name),
)

fun Iterable<ProgrammeFund>.toProgrammeFundDataModel() = map { it.toDataModel() }

fun ProgrammeFund.toDataModel() =
    ProgrammeFundData(
        id = id,
        selected = selected,
        type = ProgrammeFundTypeData.valueOf(type.name),
        abbreviation = abbreviation.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
        description = description.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet()
    )

fun Set<ProjectCallFlatRate>.toDataModel(): FlatRateSetupData {
    val groupedByType = associateBy { it.type }
    return FlatRateSetupData(
        staffCostFlatRateSetup = groupedByType[FlatRateType.STAFF_COSTS]?.toDataModel(),
        officeAndAdministrationOnStaffCostsFlatRateSetup = groupedByType[FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS]?.toDataModel(),
        officeAndAdministrationOnDirectCostsFlatRateSetup = groupedByType[FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS]?.toDataModel(),
        travelAndAccommodationOnStaffCostsFlatRateSetup = groupedByType[FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS]?.toDataModel(),
        otherCostsOnStaffCostsFlatRateSetup = groupedByType[FlatRateType.OTHER_COSTS_ON_STAFF_COSTS]?.toDataModel(),
    )
}

fun ProjectCallFlatRate.toDataModel() = FlatRateData(
    rate = rate,
    isAdjustable = isAdjustable,
)

fun MutableSet<ApplicationFormFieldConfiguration>.toDataModel() = map {
    ApplicationFormFieldConfigurationData(
        it.id, FieldVisibilityStatusData.valueOf(it.visibilityStatus.name)
    )
}.toMutableSet()


fun Iterable<ProgrammeLumpSum>.toLumpSumDataModel() = map {
    ProgrammeLumpSumListData(
        id = it.id,
        name = it.name.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
        cost = it.cost,
        splittingAllowed = it.splittingAllowed,
    )
}

fun Iterable<ProgrammeUnitCost>.toUnitCostDataModel() = sorted().map {
    ProgrammeUnitCostListData(
        id = it.id,
        name = it.name.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
        type = it.type.map { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation) }.toSet(),
        costPerUnit = it.costPerUnit,
        categories = it.categories.map { BudgetCategoryData.valueOf(it.name) }.toSet(),
    )
}

inline fun <T : TranslationEntity> Set<T>.extractField(extractFunction: (T) -> String?) =
    map { InputTranslationData(SystemLanguageData.valueOf(it.language().name), extractFunction.invoke(it)) }
        .filterTo(HashSet()) { !it.translation.isNullOrBlank() }
