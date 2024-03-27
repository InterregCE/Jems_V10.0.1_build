package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.AllowedRealCostsDTO
import io.cloudflight.jems.api.call.dto.CallChecklistDTO
import io.cloudflight.jems.api.call.dto.CallCostOptionDTO
import io.cloudflight.jems.api.call.dto.CallDTO
import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.call.dto.CallFundRateDTO
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.call.dto.CallUpdateRequestDTO
import io.cloudflight.jems.api.call.dto.PreSubmissionPluginsDTO
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.ApplicationFormFieldConfigurationDTO
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.StepSelectionOptionDTO
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.UpdateApplicationFormFieldConfigurationRequestDTO
import io.cloudflight.jems.api.call.dto.notificationConfiguration.ProjectNotificationConfigurationDTO
import io.cloudflight.jems.api.notification.dto.NotificationTypeDTO
import io.cloudflight.jems.server.call.service.model.AllowedRealCosts
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallApplicationFormFieldsConfiguration
import io.cloudflight.jems.server.call.service.model.CallChecklist
import io.cloudflight.jems.server.call.service.model.CallCostOption
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.CallFundRate
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.call.service.model.PreSubmissionPlugins
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.common.CommonDTOMapper
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.programme.controller.checklist.toDto
import io.cloudflight.jems.server.programme.controller.costoption.toDto
import io.cloudflight.jems.server.programme.controller.priority.toDto
import io.cloudflight.jems.server.programme.controller.stateaid.toDto
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

fun Page<CallSummary>.toDto() = map { it.toDto() }

fun CallSummary.toDto() = CallDTO(
    id = id,
    name = name,
    status = status,
    startDateTime = startDate,
    endDateTime = endDate,
    endDateTimeStep1 = endDateStep1
)

fun CallDetail.toDto() = CallDetailDTO(
    id = id,
    name = name,
    additionalFundAllowed = isAdditionalFundAllowed,
    directContributionsAllowed = isDirectContributionsAllowed,
    status = status,
    type = type,
    startDateTime = startDate,
    endDateTimeStep1 = endDateStep1,
    endDateTime = endDate,
    lengthOfPeriod = lengthOfPeriod,
    description = description,
    objectives = objectives.map { it.toDto() },
    strategies = strategies.sorted(),
    funds = funds.map { it.toDto() },
    stateAids = stateAids.toDto(),
    flatRates = flatRates.toDto(),
    lumpSums = lumpSums.toDto(),
    unitCosts = unitCosts.toDto(),
    applicationFormFieldConfigurations = applicationFormFieldConfigurations.toDto(type),
    preSubmissionCheckPluginKey = preSubmissionCheckPluginKey,
    firstStepPreSubmissionCheckPluginKey = firstStepPreSubmissionCheckPluginKey,
    reportPartnerCheckPluginKey = reportPartnerCheckPluginKey,
    reportProjectCheckPluginKey = reportProjectCheckPluginKey,
    controlReportPartnerCheckPluginKey = controlReportPartnerCheckPluginKey,
    controlReportSamplingCheckPluginKey = controlReportSamplingCheckPluginKey
)

fun CallUpdateRequestDTO.toModel() = Call(
    id = id ?: 0,
    name = name,
    type = type,
    isAdditionalFundAllowed = additionalFundAllowed,
    isDirectContributionsAllowed = directContributionsAllowed,
    lengthOfPeriod = lengthOfPeriod,
    startDate = startDateTime,
    endDateStep1 = endDateTimeStep1,
    endDate = endDateTime,
    description = description,
    priorityPolicies = priorityPolicies,
    strategies = strategies,
    funds = funds.map { it.toModel() }.toMutableSet(),
    stateAidIds = stateAidIds,
)

fun PreSubmissionPluginsDTO.toDTO() = callDTOMapper.map(this)
fun PreSubmissionPluginsDTO.toModel() = PreSubmissionPlugins(
    pluginKey = pluginKey,
    firstStepPluginKey = firstStepPluginKey ?: "",
    reportPartnerCheckPluginKey = reportPartnerCheckPluginKey,
    reportProjectCheckPluginKey = reportProjectCheckPluginKey,
    controlReportPartnerCheckPluginKey = controlReportPartnerCheckPluginKey,
    controlReportSamplingCheckPluginKey = controlReportSamplingCheckPluginKey
)

fun CallCostOptionDTO.toModel() = CallCostOption(
    projectDefinedUnitCostAllowed = projectDefinedUnitCostAllowed,
    projectDefinedLumpSumAllowed = projectDefinedLumpSumAllowed,
)

fun CallCostOption.toDto() = CallCostOptionDTO(
    projectDefinedUnitCostAllowed = projectDefinedUnitCostAllowed,
    projectDefinedLumpSumAllowed = projectDefinedLumpSumAllowed,
)

fun CallApplicationFormFieldsConfiguration.toDto() =
    callDTOMapper.map(this)

fun List<ProjectNotificationConfiguration>.toDto() = map { it.toDto() }

fun List<ProjectNotificationConfigurationDTO>.toNotificationModel() = map { it.toModel() }

fun MutableSet<ApplicationFormFieldConfiguration>.toDto(callType: CallType) =
    map { callDTOMapper.map(it, callType) }.toMutableSet()

fun MutableSet<UpdateApplicationFormFieldConfigurationRequestDTO>.toModel() =
    callDTOMapper.mapUpdateRequest(this)

fun StepSelectionOptionDTO.toModel() =
    callDTOMapper.map(this)

fun FieldVisibilityStatus.toDTO() =
    callDTOMapper.map(this)

fun AllowedRealCosts.toDto() = callDTOMapper.map(this)
fun AllowedRealCostsDTO.toModel() = callDTOMapper.map(this)

fun CallFundRate.toDto() = callDTOMapper.map(this)
fun CallFundRateDTO.toModel() = callDTOMapper.map(this)

fun NotificationType.toDto() = callDTOMapper.map(this)
fun NotificationTypeDTO.toModel() = callDTOMapper.map(this)

fun ProjectNotificationConfiguration.toDto() = ProjectNotificationConfigurationDTO(
    id = id.toDto(),
    active = active,
    sendToManager = sendToManager,
    sendToLeadPartner = sendToLeadPartner,
    sendToProjectPartners = sendToProjectPartners,
    sendToProjectAssigned = sendToProjectAssigned,
    sendToControllers = sendToControllers,
    emailSubject = emailSubject,
    emailBody = emailBody,
)

fun ProjectNotificationConfigurationDTO.toModel() = ProjectNotificationConfiguration(
    id = id.toModel(),
    active = active,
    sendToManager = sendToManager,
    sendToLeadPartner = sendToLeadPartner,
    sendToProjectPartners = sendToProjectPartners,
    sendToProjectAssigned = sendToProjectAssigned,
    sendToControllers = sendToControllers,
    emailSubject = emailSubject,
    emailBody = emailBody,
)

fun CallChecklist.toDTO() = CallChecklistDTO(
    id = id,
    name = name,
    type = type.toDto(),
    lastModificationDate = lastModificationDate,
    selected = selected
)

private val callDTOMapper = Mappers.getMapper(CallDTOMapper::class.java)

@Mapper(uses = [CommonDTOMapper::class])
abstract class CallDTOMapper {

    abstract fun map(fieldVisibilityStatus: FieldVisibilityStatus): StepSelectionOptionDTO
    abstract fun map(stepSelectionOptionDTO: StepSelectionOptionDTO): FieldVisibilityStatus

    abstract fun map(allowedRealCostsDTO: AllowedRealCostsDTO): AllowedRealCosts
    abstract fun map(allowedRealCosts: AllowedRealCosts): AllowedRealCostsDTO

    abstract fun map(notificationTypeDTO: NotificationTypeDTO): NotificationType
    abstract fun map(notificationType: NotificationType): NotificationTypeDTO

    abstract fun map(callFundRateDTO: CallFundRateDTO): CallFundRate
    abstract fun map(callFundRate: CallFundRate): CallFundRateDTO

    abstract fun map(preSubmissionPluginsDTO: PreSubmissionPluginsDTO): PreSubmissionPlugins
    abstract fun map(preSubmissionPlugins: PreSubmissionPlugins): PreSubmissionPluginsDTO
    fun mapUpdateRequest(
        updateApplicationFormFieldConfigurationDTOs: MutableSet<UpdateApplicationFormFieldConfigurationRequestDTO>,
    ): MutableSet<ApplicationFormFieldConfiguration> =
        updateApplicationFormFieldConfigurationDTOs.map {
            ApplicationFormFieldConfiguration(
                it.id,
                if (!it.visible) FieldVisibilityStatus.NONE else it.availableInStep.toModel()
            )
        }.toMutableSet()

    fun map(applicationFormFieldConfiguration: ApplicationFormFieldConfiguration, callType: CallType) =
        ApplicationFormFieldConfigurationDTO(
            applicationFormFieldConfiguration.id,
            visible = applicationFormFieldConfiguration.visibilityStatus != FieldVisibilityStatus.NONE,
            visibilityLocked = !applicationFormFieldConfiguration.getValidVisibilityStatusSet(callType)
                .contains(FieldVisibilityStatus.NONE),
            availableInStep = applicationFormFieldConfiguration.visibilityStatus.toDTO(),
            stepSelectionLocked = !applicationFormFieldConfiguration.getValidVisibilityStatusSet(callType)
                .containsAll(listOf(FieldVisibilityStatus.STEP_ONE_AND_TWO, FieldVisibilityStatus.STEP_TWO_ONLY))
        )

    fun map(
        callApplicationFormFieldsConfiguration: CallApplicationFormFieldsConfiguration,
    ): MutableSet<ApplicationFormFieldConfigurationDTO> {
        val callType = callApplicationFormFieldsConfiguration.callType
        return callApplicationFormFieldsConfiguration.applicationFormFieldConfigurations.map { applicationFormFieldConfiguration ->
            this.map(applicationFormFieldConfiguration, callType)
        }.toMutableSet()
    }
}
