package io.cloudflight.jems.server.call.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.InputCallCreate
import io.cloudflight.jems.api.call.dto.OutputCall
import io.cloudflight.jems.api.call.dto.OutputCallList
import io.cloudflight.jems.api.call.dto.OutputCallWithDates
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.controller.toDto
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.CallTranslEntity
import io.cloudflight.jems.server.call.entity.CallTranslId
import io.cloudflight.jems.server.call.repository.flatrate.toProjectCallFlatRate
import io.cloudflight.jems.server.programme.controller.costoption.toDto
import io.cloudflight.jems.server.programme.entity.ProgrammeFundEntity
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.service.toOutputProgrammeFund
import io.cloudflight.jems.server.programme.service.toOutputProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.entity.Strategy
import io.cloudflight.jems.server.programme.repository.costoption.toProgrammeUnitCost

/**
 * Map InputCallCreate to entity Call.
 * The specified minute is included in the end time of the call by adding all of its seconds.
 */
fun InputCallCreate.toEntity(
    creator: User,
    priorityPolicies: Set<ProgrammeSpecificObjectiveEntity>,
    strategies: Set<Strategy>,
    funds: Set<ProgrammeFundEntity>
) = CallEntity(
    creator = creator,
    name = name!!,
    prioritySpecificObjectives = priorityPolicies,
    strategies = strategies,
    funds = funds,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    status = CallStatus.DRAFT,
    startDate = startDate!!.withSecond(0).withNano(0),
    endDate = endDate!!.withSecond(0).withNano(0).plusMinutes(1).minusNanos(1),
    lengthOfPeriod = lengthOfPeriod
)

fun CallEntity.toOutputCall() = OutputCall(
    id = id,
    name = name,
    priorityPolicies = prioritySpecificObjectives.map { it.toOutputProgrammePriorityPolicy() },
    strategies = strategies.map { it.strategy },
    funds = funds.map { it.toOutputProgrammeFund() },
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    status = status,
    startDate = startDate,
    endDate = endDate,
    description = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) },
    lengthOfPeriod = lengthOfPeriod,
    flatRates = flatRates.toProjectCallFlatRate().toDto(),
    lumpSums = lumpSums.map { it.toProgrammeUnitCost().toDto() },
    unitCosts = unitCosts.map { it.toProgrammeUnitCost().toDto() },
)

fun CallEntity.toOutputCallList() = OutputCallList(
    id = id,
    name = name,
    status = status,
    startDate = startDate,
    endDate = endDate
)

fun CallEntity.toOutputCallWithDates() = OutputCallWithDates(
    id = id,
    name = name,
    startDate = startDate,
    endDate = endDate,
    lengthOfPeriod = lengthOfPeriod,
    flatRates = flatRates.toProjectCallFlatRate().toDto()
)

fun Set<InputTranslation>.toDescriptionEntity(callId: Long) = mapTo(HashSet()) {
    CallTranslEntity(CallTranslId(callId, it.language), it.translation)
}
