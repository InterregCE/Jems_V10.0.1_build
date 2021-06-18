package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.CallDTO
import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.call.dto.CallUpdateRequestDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormConfigurationDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormConfigurationSummaryDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormFieldConfigurationDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.UpdateApplicationFormConfigurationRequestDTO
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfigurationSummary
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.programme.controller.costoption.toDto
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.programme.controller.priority.toDto
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
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    status = status,
    startDateTime = startDate,
    endDateTimeStep1 = endDateStep1,
    endDateTime = endDate,
    lengthOfPeriod = lengthOfPeriod,
    description = description,
    objectives = objectives.map { it.toDto() },
    strategies = strategies.sorted(),
    funds = funds.toDto(),
    flatRates = flatRates.toDto(),
    lumpSums = lumpSums.toDto(),
    unitCosts = unitCosts.toDto(),
)

fun CallUpdateRequestDTO.toModel() = Call(
    id = id ?: 0,
    name = name,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    lengthOfPeriod = lengthOfPeriod,
    startDate = startDateTime,
    endDateStep1 = endDateTimeStep1,
    endDate = endDateTime,
    description = description,
    priorityPolicies = priorityPolicies,
    strategies = strategies,
    fundIds = fundIds,
)

fun List<ApplicationFormConfigurationSummary>.toDTO() =
    map { it.toApplicationFormConfigurationSummaryDTO() }

fun ApplicationFormConfigurationSummary.toApplicationFormConfigurationSummaryDTO() =
    callDTOMapper.mapToSummaryDTO(this)

fun ApplicationFormConfiguration.toDTO() =
    callDTOMapper.map(this)

fun UpdateApplicationFormConfigurationRequestDTO.toModel() =
    callDTOMapper.map(this)


private val callDTOMapper = Mappers.getMapper(CallDTOMapper::class.java)

@Mapper
abstract class CallDTOMapper {

    abstract fun mapToSummaryDTO(applicationFormConfigurationSummary: ApplicationFormConfigurationSummary): ApplicationFormConfigurationSummaryDTO
    abstract fun map(applicationFormConfiguration: ApplicationFormConfiguration): ApplicationFormConfigurationDTO
    abstract fun map(applicationFormConfigurationDTO: UpdateApplicationFormConfigurationRequestDTO): ApplicationFormConfiguration
    abstract fun map(applicationFormFieldConfigurationDTOs: MutableSet<ApplicationFormFieldConfigurationDTO>): MutableSet<ApplicationFormFieldConfiguration>
}
