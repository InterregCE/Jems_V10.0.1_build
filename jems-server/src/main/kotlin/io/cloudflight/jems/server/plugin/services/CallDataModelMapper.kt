package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.plugin.contract.models.call.ApplicationFormFieldConfigurationData
import io.cloudflight.jems.plugin.contract.models.call.CallDetailData
import io.cloudflight.jems.plugin.contract.models.call.CallStatusData
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
import io.cloudflight.jems.server.call.service.model.CallFundRate
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import java.util.*

fun CallDetail.toDataModel(inputLanguages: List<ProgrammeLanguage>) =
    pluginCallDataMapper.map(this, inputLanguages.toSystemLanguageData())

fun ProgrammePriority.toDataModel() =
    pluginCallDataMapper.map(this)

fun ProgrammeSpecificObjective.toDataModel() =
    pluginCallDataMapper.map(this)

fun ProgrammeFund.toDataModel() =
    pluginCallDataMapper.map(this)

fun ProjectCallFlatRate.toDataModel() =
    pluginCallDataMapper.map(this)

fun MutableSet<ApplicationFormFieldConfiguration>.toDataModel() =
    pluginCallDataMapper.map(this)

fun List<ProgrammeLanguage>.toSystemLanguageData()=
    map { SystemLanguageData.valueOf(it.code.name) }.toSet()

private val pluginCallDataMapper = Mappers.getMapper(PluginCallDataMapper::class.java)

@Mapper
abstract class PluginCallDataMapper {

    abstract fun map(projectCallFlatRate: ProjectCallFlatRate): FlatRateData
    abstract fun map(programmeSpecificObjective: ProgrammeSpecificObjective): ProgrammeSpecificObjectiveData
    abstract fun map(programmeObjectivePolicy: ProgrammeObjectivePolicy): ProgrammeObjectivePolicyData
    abstract fun map(programmeObjective: ProgrammeObjective): ProgrammeObjectiveData
    abstract fun map(programmePriority: ProgrammePriority): ProgrammePriorityData
    abstract fun map(programmeFundType: ProgrammeFundType): ProgrammeFundTypeData
    abstract fun map(programmeFund: ProgrammeFund): ProgrammeFundData
    abstract fun map(budgetCategory: BudgetCategory): BudgetCategoryData
    abstract fun map(programmeUnitCost: Iterable<ProgrammeUnitCost>): List<ProgrammeUnitCostListData>
    abstract fun mapProgrammeLumpSum(programmeUnitCost: Iterable<ProgrammeLumpSum>): List<ProgrammeLumpSumListData>
    abstract fun map(applicationFormFieldConfiguration: MutableSet<ApplicationFormFieldConfiguration>): MutableSet<ApplicationFormFieldConfigurationData>
    abstract fun map(callStatus: CallStatus): CallStatusData
    @Mappings(
        Mapping(source = "inputLanguages", target = "inputLanguages"),
        Mapping(source = "callDetail.startDate", target = "startDateTime"),
        Mapping(source = "callDetail.endDateStep1", target = "endDateTimeStep1"),
        Mapping(source = "callDetail.endDate", target = "endDateTime"),
        Mapping(source = "callDetail.additionalFundAllowed", target = "isAdditionalFundAllowed"),
    )
    abstract fun map(callDetail: CallDetail, inputLanguages: Set<SystemLanguageData>): CallDetailData
    abstract fun map(programmeStrategy: SortedSet<ProgrammeStrategy>): SortedSet<ProgrammeStrategyData>

    fun map(callFundRate: CallFundRate): ProgrammeFundData =
        map(callFundRate.programmeFund)

    fun mapInputTranslation(inputTranslation: Set<InputTranslation>): Set<InputTranslationData> =
        inputTranslation.toDataModel()

    fun mapFlatRateSetupData(projectCallFlatRate: Set<ProjectCallFlatRate>): FlatRateSetupData {
        val groupedByType = projectCallFlatRate.associateBy { it.type }
        return FlatRateSetupData(
            staffCostFlatRateSetup = groupedByType[FlatRateType.STAFF_COSTS]?.toDataModel(),
            officeAndAdministrationOnStaffCostsFlatRateSetup = groupedByType[FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS]?.toDataModel(),
            officeAndAdministrationOnDirectCostsFlatRateSetup = groupedByType[FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS]?.toDataModel(),
            travelAndAccommodationOnStaffCostsFlatRateSetup = groupedByType[FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS]?.toDataModel(),
            otherCostsOnStaffCostsFlatRateSetup = groupedByType[FlatRateType.OTHER_COSTS_ON_STAFF_COSTS]?.toDataModel(),
        )
    }
}
