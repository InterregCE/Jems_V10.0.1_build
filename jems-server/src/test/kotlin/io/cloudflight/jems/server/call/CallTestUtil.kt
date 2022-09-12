package io.cloudflight.jems.server.call

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.call.entity.AllowedRealCostsEntity
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationEntity
import io.cloudflight.jems.server.call.entity.ApplicationFormFieldConfigurationId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.CallFundRateEntity
import io.cloudflight.jems.server.call.entity.CallTranslEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.FundSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.repository.toModel
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.CallFundRate
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserStatus
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

val START = ZonedDateTime.now().withSecond(0).withNano(0)
val END = ZonedDateTime.now().plusDays(5).withSecond(0).withNano(0).plusMinutes(1).minusNanos(1)
private const val STATE_AID_ID = 23L
private const val LUMP_SUM_ID = 4L
private const val UNIT_COST_ID = 3L


private val account = UserEntity(
    id = 1,
    email = "admin@admin.dev",
    name = "Name",
    surname = "Surname",
    userRole = UserRoleEntity(id = 1, name = "ADMIN"),
    password = "hash_pass",
    userStatus = UserStatus.ACTIVE
)

fun createTestCallEntity(
    id: Long,
    creator: UserEntity = account,
    name: String = "Test call name",
    status: CallStatus = CallStatus.DRAFT,
    type: CallType = CallType.STANDARD,
    startDate: ZonedDateTime = START,
    endDateStep1: ZonedDateTime? = null,
    endDate: ZonedDateTime = END,
    lengthOfPeriod: Int = 1,
    isAdditionalFundAllowed: Boolean = false,
    translatedValues: MutableSet<CallTranslEntity> = mutableSetOf(),
    prioritySpecificObjectives: MutableSet<ProgrammeSpecificObjectiveEntity> = mutableSetOf(),
    strategies: MutableSet<ProgrammeStrategyEntity> = mutableSetOf(),
    funds: MutableSet<CallFundRateEntity> = mutableSetOf(),
    flatRates: MutableSet<ProjectCallFlatRateEntity> = mutableSetOf(),
    allowedRealCosts:  AllowedRealCostsEntity = defaultAllowedRealCostsByCallType(type),
    unitCosts: MutableSet<ProgrammeUnitCostEntity> = mutableSetOf()
): CallEntity {
    val call = CallEntity(
        id = id,
        creator = creator,
        name = name,
        status = status,
        type = type,
        startDate = startDate,
        endDateStep1 = endDateStep1,
        endDate = endDate,
        lengthOfPeriod = lengthOfPeriod,
        isAdditionalFundAllowed = isAdditionalFundAllowed,
        translatedValues = translatedValues,
        prioritySpecificObjectives = prioritySpecificObjectives,
        strategies = strategies,
        funds = funds,
        flatRates = flatRates,
        allowedRealCosts = allowedRealCosts,
        preSubmissionCheckPluginKey = null,
        firstStepPreSubmissionCheckPluginKey = null,
        unitCosts = unitCosts,
        projectDefinedUnitCostAllowed = true,
        projectDefinedLumpSumAllowed = false,
    ).apply {
        translatedValues.add(CallTranslEntity(TranslationId(this, SystemLanguage.EN), "This is a dummy call"))
        flatRates.add(
            ProjectCallFlatRateEntity(
                setupId = FlatRateSetupId(call = this, type = FlatRateType.STAFF_COSTS),
                rate = 5,
                isAdjustable = true
            )
        )
    }
    call.translatedValues.add(CallTranslEntity(TranslationId(call, SystemLanguage.EN), "This is a dummy call"))
    return call
}

fun createCallDetailModel(
    id: Long,
    name: String,
    status: CallStatus = CallStatus.DRAFT,
    type: CallType = CallType.STANDARD,
    startDate: ZonedDateTime = START,
    endDateStep1: ZonedDateTime? = null,
    endDate: ZonedDateTime = END,
    isAdditionalFundAllowed: Boolean = false,
    lengthOfPeriod: Int = 1,
    description: Set<InputTranslation> = setOf(InputTranslation(SystemLanguage.EN, "This is a dummy call")),
    objectives: List<ProgrammePriority> = defaultObjectives,
    strategies: SortedSet<ProgrammeStrategy> = defaultStrategies,
    funds: SortedSet<CallFundRate> = sortedSetOf(),
    stateAids: List<ProgrammeStateAid> = defaultStateAids,
    flatRates: SortedSet<ProjectCallFlatRate> = defaultFlatRates,
    lumpSums: List<ProgrammeLumpSum> = defaultLumpSums,
    unitCosts: List<ProgrammeUnitCost> = defaultUnitCosts,
    applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration> =
        applicationFormFieldConfigurationEntities(createTestCallEntity(id,  name = name)).toModel(),
    preSubmissionCheckPluginKey: String? = null,
    firstStepPreSubmissionCheckPluginKey: String? = null
): CallDetail {
    return CallDetail(
        id = id,
        name = name,
        status = status,
        type = type,
        startDate = startDate,
        endDateStep1 = endDateStep1,
        endDate = endDate,
        isAdditionalFundAllowed = isAdditionalFundAllowed,
        lengthOfPeriod = lengthOfPeriod,
        description = description,
        objectives = objectives,
        strategies = strategies,
        funds = funds,
        stateAids =  stateAids,
        flatRates = flatRates,
        lumpSums = lumpSums,
        unitCosts = unitCosts,
        applicationFormFieldConfigurations = applicationFormFieldConfigurations,
        preSubmissionCheckPluginKey = preSubmissionCheckPluginKey,
        firstStepPreSubmissionCheckPluginKey = firstStepPreSubmissionCheckPluginKey,
        projectDefinedUnitCostAllowed = true,
        projectDefinedLumpSumAllowed = false,
    )
}

fun partnerWithId(id: Long) = ProjectPartnerEntity(
    id = id,
    project = dummyProject,
    abbreviation = "test abbr",
    role = ProjectPartnerRole.LEAD_PARTNER,
    legalStatus = ProgrammeLegalStatusEntity()
)

fun userWithId(id: Long) = LocalCurrentUser(
    user = User(
        id = id,
        email = "x@y",
        name = "",
        surname = "",
        userRole = UserRole(0, "", permissions = emptySet(), isDefault = false),
        userStatus = UserStatus.ACTIVE
    ),
    password = "hash_pass",
    authorities = emptyList(),
)

fun callFundRate(fundId: Long) = CallFundRate(
    programmeFund = ProgrammeFund(id = fundId, selected = true),
    rate = BigDecimal.TEN,
    adjustable = true
)

fun callFundRateEntity(call: CallEntity, fundId: Long) = CallFundRateEntity(
    setupId = FundSetupId(call, ProgrammeFundEntity(id = fundId, selected = true)),
    rate = BigDecimal.TEN,
    isAdjustable = true
)

fun callFund(fundId: Long) = ProgrammeFund(
    id = fundId,
    selected = true
)

fun allowedReadCostsEntity(
    allowRealStaffCosts: Boolean = true,
    allowRealTravelAndAccommodationCosts: Boolean = true,
    allowRealExternalExpertiseAndServicesCosts: Boolean = true,
    allowRealEquipmentCosts: Boolean = true,
    allowRealInfrastructureCosts: Boolean = true

) : AllowedRealCostsEntity {
    return AllowedRealCostsEntity(
        allowRealStaffCosts = allowRealStaffCosts,
        allowRealTravelAndAccommodationCosts = allowRealTravelAndAccommodationCosts,
        allowRealExternalExpertiseAndServicesCosts = allowRealExternalExpertiseAndServicesCosts,
        allowRealEquipmentCosts = allowRealEquipmentCosts,
        allowRealInfrastructureCosts = allowRealInfrastructureCosts
    )
}

fun callDetail(
    id : Long = 10L,
    name : String = "call name",
    status : CallStatus = CallStatus.PUBLISHED,
    type: CallType = CallType.STANDARD,
    startDate : ZonedDateTime = ZonedDateTime.of(2020,1,10,10,10,10,10, ZoneId.systemDefault()),
    endDateStep1 : ZonedDateTime = ZonedDateTime.of(2020,1,15,10,10,10,10, ZoneId.systemDefault()),
    endDate : ZonedDateTime = ZonedDateTime.of(2020,1,30,15,10,10,10, ZoneId.systemDefault()),
    isAdditionalFundAllowed : Boolean = true,
    lengthOfPeriod : Int = 12,
    applicationFormFieldConfigurations : MutableSet<ApplicationFormFieldConfiguration> = mutableSetOf(),
    preSubmissionCheckPluginKey: String? = null,
    firstStepPreSubmissionCheckPluginKey: String? = null
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
    applicationFormFieldConfigurations =  applicationFormFieldConfigurations,
    preSubmissionCheckPluginKey = preSubmissionCheckPluginKey,
    firstStepPreSubmissionCheckPluginKey = firstStepPreSubmissionCheckPluginKey,
    projectDefinedUnitCostAllowed = false,
    projectDefinedLumpSumAllowed = true,
)

fun defaultAllowedRealCostsByCallType(callType: CallType) : AllowedRealCostsEntity {
    return when (callType) {
        CallType.STANDARD -> allowedReadCostsEntity()
        CallType.SPF -> allowedReadCostsEntity(
            false,
            false,
            false,
            false
        )
    }
}

private val dummyProject = ProjectEntity(
    id = 1,
    call = createTestCallEntity(0, name = "Test Call"),
    acronym = "Test Project",
    applicant = account,
    currentStatus = ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = account),
)

private val defaultObjectives = listOf(
    ProgrammePriority(
        id = 0L,
        code = "PRIO_CODE",
        objective = ProgrammeObjective.PO1,
        specificObjectives = listOf(
            ProgrammeSpecificObjective(ProgrammeObjectivePolicy.AdvancedTechnologies, "CODE_ADVA"),
            ProgrammeSpecificObjective(ProgrammeObjectivePolicy.Digitisation, "CODE_DIGI"),
        )
    )
)

private val defaultStrategies = sortedSetOf(
    ProgrammeStrategy.EUStrategyBalticSeaRegion,
    ProgrammeStrategy.AtlanticStrategy
)


private val defaultStateAids = listOf(
    ProgrammeStateAid(
        id = STATE_AID_ID,
        measure = ProgrammeStateAidMeasure.OTHER_1,
        threshold = BigDecimal.ZERO,
        maxIntensity = BigDecimal.ZERO,
        name = emptySet(),
        abbreviatedName = emptySet(),
        schemeNumber = ""
    )
)

private val defaultFlatRates = sortedSetOf(
    ProjectCallFlatRate(
        type = FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS,
        rate = 15,
        adjustable = true
    )
)

private val defaultLumpSums = listOf(
    ProgrammeLumpSum(
        id = LUMP_SUM_ID,
        cost = BigDecimal.ONE,
        splittingAllowed = true,
        phase = ProgrammeLumpSumPhase.Closure,
        categories = setOf(BudgetCategory.InfrastructureCosts),
        isFastTrack = false
    )
)

private val defaultUnitCosts = listOf(
    ProgrammeUnitCost(
        id = UNIT_COST_ID,
        projectId = null,
        costPerUnit = BigDecimal.TEN,
        isOneCostCategory = true,
        categories = setOf(BudgetCategory.InfrastructureCosts),
        costPerUnitForeignCurrency = BigDecimal.ZERO,
        foreignCurrencyCode = null
    )
)

private fun applicationFormFieldConfigurationEntities(callEntity: CallEntity) = mutableSetOf(
    ApplicationFormFieldConfigurationEntity(
        ApplicationFormFieldConfigurationId("fieldId", callEntity),
        FieldVisibilityStatus.STEP_ONE_AND_TWO
    )
)
