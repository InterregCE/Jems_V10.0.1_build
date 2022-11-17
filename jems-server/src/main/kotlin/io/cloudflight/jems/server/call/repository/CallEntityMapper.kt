package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.entity.AllowedRealCostsEntity
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationEntity
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.CallFundRateEntity
import io.cloudflight.jems.server.call.entity.CallTranslEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.entity.ProjectCallStateAidEntity
import io.cloudflight.jems.server.call.entity.StateAidSetupId
import io.cloudflight.jems.server.call.service.model.AllowedRealCosts
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.CallFundRate
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import io.cloudflight.jems.server.programme.repository.costoption.toModel
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.repository.priority.toModel
import io.cloudflight.jems.server.programme.repository.stateaid.toModel
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.project.repository.toModel
import io.cloudflight.jems.server.user.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page
import java.util.*

fun CallEntity.toModel() = CallSummary(
    id = id,
    name = name,
    status = status,
    startDate = startDate,
    endDate = endDate,
    endDateStep1 = endDateStep1
)

fun Page<CallEntity>.toModel() = map { it.toModel() }

fun CallEntity.toDetailModel(
    applicationFormFieldConfigurationEntities: MutableSet<ApplicationFormFieldConfigurationEntity>,
    stateAids: Collection<ProjectCallStateAidEntity>
) = CallDetail(
    id = id,
    name = name,
    status = status,
    type = type,
    startDate = startDate,
    endDateStep1 = endDateStep1,
    endDate = endDate,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    lengthOfPeriod = lengthOfPeriod,
    description = translatedValues.extractField { it.description },
    objectives = prioritySpecificObjectives.groupSpecificObjectives(),
    strategies = strategies.mapTo(TreeSet()) { it.strategy },
    funds = funds.mapTo(TreeSet()) { it.toModel() },
    stateAids = stateAids.map { it.setupId.stateAid.toModel() },
    flatRates = flatRates.toModel(),
    lumpSums = lumpSums.toModel(),
    unitCosts = unitCosts.toModel(),
    applicationFormFieldConfigurations = applicationFormFieldConfigurationEntities.toModel(),
    preSubmissionCheckPluginKey = preSubmissionCheckPluginKey,
    firstStepPreSubmissionCheckPluginKey = firstStepPreSubmissionCheckPluginKey,
    projectDefinedUnitCostAllowed = projectDefinedUnitCostAllowed,
    projectDefinedLumpSumAllowed = projectDefinedLumpSumAllowed,
)

private fun Set<ProgrammeSpecificObjectiveEntity>.groupSpecificObjectives() =
    groupBy { it.programmePriority!!.id }.values.map {
        ProgrammePriority(
            id = it.first().programmePriority!!.id,
            code = it.first().programmePriority!!.code,
            title = it.first().programmePriority!!.translatedValues.mapTo(HashSet()) {
                InputTranslation(
                    it.translationId.language,
                    it.title
                )
            },
            objective = it.first().programmePriority!!.objective,
            specificObjectives = it.sortedBy { it.programmeObjectivePolicy }.map { it.toModel() }
        )
    }.sortedBy { it.id }

fun Call.toEntity(
    user: UserEntity,
    retrieveSpecificObjective: (ProgrammeObjectivePolicy) -> ProgrammeSpecificObjectiveEntity,
    retrieveStrategies: (Set<ProgrammeStrategy>) -> Set<ProgrammeStrategyEntity>,
    existingEntity: CallEntity? = null
) = CallEntity(
    id = id,
    creator = user,
    name = name,
    status = status!!,
    type = type,
    startDate = startDate,
    endDateStep1 = endDateStep1,
    endDate = endDate,
    lengthOfPeriod = lengthOfPeriod,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    translatedValues = mutableSetOf(),
    prioritySpecificObjectives = priorityPolicies.mapTo(HashSet()) { retrieveSpecificObjective.invoke(it) },
    strategies = if (strategies.isEmpty()) mutableSetOf() else retrieveStrategies.invoke(strategies).toMutableSet(),
    funds = existingEntity?.funds ?: mutableSetOf(),
    flatRates = existingEntity?.flatRates ?: mutableSetOf(),
    lumpSums = existingEntity?.lumpSums ?: mutableSetOf(),
    unitCosts = existingEntity?.unitCosts ?: mutableSetOf(),
    allowedRealCosts = existingEntity?.allowedRealCosts ?: getDefaultAllowedRealCosts(type),
    preSubmissionCheckPluginKey = existingEntity?.preSubmissionCheckPluginKey,
    firstStepPreSubmissionCheckPluginKey = existingEntity?.firstStepPreSubmissionCheckPluginKey,
    projectDefinedUnitCostAllowed = existingEntity?.projectDefinedUnitCostAllowed ?: false,
    projectDefinedLumpSumAllowed = existingEntity?.projectDefinedLumpSumAllowed ?: false,
).apply {
    translatedValues.addAll(description.combineDescriptionsToTranslations(this))
}

fun AllowedRealCosts.toEntity() = AllowedRealCostsEntity(
    allowRealStaffCosts = allowRealStaffCosts,
    allowRealTravelAndAccommodationCosts = allowRealTravelAndAccommodationCosts,
    allowRealExternalExpertiseAndServicesCosts = allowRealExternalExpertiseAndServicesCosts,
    allowRealEquipmentCosts = allowRealEquipmentCosts,
    allowRealInfrastructureCosts = allowRealInfrastructureCosts
)

fun AllowedRealCostsEntity.toModel() = AllowedRealCosts(
    allowRealStaffCosts = allowRealStaffCosts,
    allowRealTravelAndAccommodationCosts = allowRealTravelAndAccommodationCosts,
    allowRealExternalExpertiseAndServicesCosts = allowRealExternalExpertiseAndServicesCosts,
    allowRealEquipmentCosts = allowRealEquipmentCosts,
    allowRealInfrastructureCosts = allowRealInfrastructureCosts
)

fun Set<InputTranslation>.combineDescriptionsToTranslations(call: CallEntity): Set<CallTranslEntity> =
    mapTo(HashSet()) {
        CallTranslEntity(
            translationId = TranslationId(sourceEntity = call, language = it.language),
            description = it.translation
        )
    }

fun Set<ProjectCallFlatRateEntity>.toModel() = mapTo(TreeSet()) {
    ProjectCallFlatRate(
        type = it.setupId.type,
        rate = it.rate,
        adjustable = it.isAdjustable
    )
}

fun Set<ProjectCallFlatRate>.toEntity(call: CallEntity) = mapTo(HashSet()) {
    ProjectCallFlatRateEntity(
        setupId = FlatRateSetupId(call = call, type = it.type),
        rate = it.rate,
        isAdjustable = it.adjustable
    )
}

fun CallFundRateEntity.toModel() = CallFundRate(
    programmeFund = setupId.programmeFund.toModel(),
    rate = rate,
    adjustable = isAdjustable
)

fun MutableSet<ApplicationFormFieldConfigurationEntity>.toModel() =
    callEntityMapper.map(this)

fun MutableSet<ApplicationFormFieldConfiguration>.toEntities(call: CallEntity) =
    map { callEntityMapper.map(call, it) }.toMutableSet()

fun Collection<ProgrammeStateAidEntity>.toEntities(call: CallEntity) =
    map { ProjectCallStateAidEntity(StateAidSetupId(call, it)) }

fun MutableSet<ProjectCallStateAidEntity>.toModel() = map { it.setupId.stateAid.toModel() }

fun List<CallEntity>.toIdNamePair() =
    callEntityMapper.map(this)

private fun getDefaultAllowedRealCosts(callType: CallType) : AllowedRealCostsEntity {
    return when (callType) {
        CallType.STANDARD -> AllowedRealCostsEntity()
        CallType.SPF -> AllowedRealCostsEntity(
            allowRealInfrastructureCosts = false
        )
    }
}

private val callEntityMapper = Mappers.getMapper(CallEntityMapper::class.java)

@Mapper
abstract class CallEntityMapper {

    abstract fun map(calls: List<CallEntity>): List<IdNamePair>

    @Mapping(source = "id.id", target = "id")
    abstract fun map(applicationFormFieldConfigurationEntity: ApplicationFormFieldConfigurationEntity): ApplicationFormFieldConfiguration

    abstract fun map(applicationFormFieldConfigurationEntities: MutableSet<ApplicationFormFieldConfigurationEntity>): MutableSet<ApplicationFormFieldConfiguration>
    fun map(
        call: CallEntity,
        fieldConfiguration: ApplicationFormFieldConfiguration
    ): ApplicationFormFieldConfigurationEntity =
        ApplicationFormFieldConfigurationEntity(
            ApplicationFormFieldConfigurationId(fieldConfiguration.id, call),
            fieldConfiguration.visibilityStatus
        )

}
