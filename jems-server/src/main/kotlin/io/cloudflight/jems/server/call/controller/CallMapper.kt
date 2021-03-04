package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.CallDTO
import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.call.dto.CallUpdateRequestDTO
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.programme.controller.costoption.toDto
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.programme.controller.priority.toDto
import org.springframework.data.domain.Page

fun Page<CallSummary>.toDto() = map { it.toDto() }

fun CallSummary.toDto() = CallDTO(
    id = id,
    name = name,
    status = status,
    startDateTime = startDate,
    endDateTime = endDate,
)

fun CallDetail.toDto() = CallDetailDTO(
    id = id,
    name = name,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    status = status,
    startDateTime = startDate,
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
    endDate = endDateTime,
    description = description,
    priorityPolicies = priorityPolicies,
    strategies = strategies,
    fundIds = fundIds,
)
