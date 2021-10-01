package io.cloudflight.jems.server.call.service.update_call

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.callUpdated
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.call.service.validator.CallValidator
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class UpdateCall(
    private val persistence: CallPersistence,
    private val callValidator: CallValidator,
    private val auditPublisher: ApplicationEventPublisher,
) : UpdateCallInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(UpdateCallException::class)
    override fun updateCall(call: Call): CallDetail {
        callValidator.validateCommonCall(call = call)
        validateUniqueName(callId = call.id, callIdWithThisName = persistence.getCallIdForNameIfExists(call.name))

        val existingCall = persistence.getCallById(callId = call.id)
        validateCorrectStatus(call = call, oldCall = existingCall)
        call.status = existingCall.status

        if (existingCall.isPublished())
            validateAllowedChanges(call, existingCall)

        return persistence.updateCall(call).also {
            auditPublisher.publishEvent(callUpdated(this, existingCall, it))
        }
    }

    private fun validateUniqueName(callId: Long, callIdWithThisName: Long?) {
        if (callIdWithThisName != null && callIdWithThisName != callId)
            throw CallNameNotUnique()
    }

    private fun validateCorrectStatus(call: Call, oldCall: CallDetail) {
        if (call.status != null && call.status != oldCall.status)
            throw CallStatusChangeForbidden()
    }

    private fun validateAllowedChanges(call: Call, oldCall: CallDetail) {
        val startDateChanged = call.startDate.toInstant() != oldCall.startDate.toInstant()
        val lengthOfPeriodChanged = call.lengthOfPeriod != oldCall.lengthOfPeriod
        val isAdditionalFundAllowedDisabled = oldCall.isAdditionalFundAllowed && !call.isAdditionalFundAllowed

        val newSpecificObjectives: Set<ProgrammeObjectivePolicy> = call.priorityPolicies
        val newFundIds = call.funds.map { it.programmeFund.id }
        val newStateAidIds = call.stateAidIds
        val oldSpecificObjectives = oldCall.objectives.mergeAllSpecificObjectives()
        val oldFundIds = oldCall.funds.mapTo(HashSet()) { it.programmeFund.id }
        val oldStateAidIds = oldCall.stateAids.mapTo(HashSet()) { it.id }

        val specificObjectivesRemoved = !newSpecificObjectives.containsAll(oldSpecificObjectives)
        val strategiesRemoved = !call.strategies.containsAll(oldCall.strategies)
        val fundsRemoved = !newFundIds.containsAll(oldFundIds)
        val stateAidsRemoved = !newStateAidIds.containsAll(oldStateAidIds)

        if (startDateChanged
            || lengthOfPeriodChanged
            || isAdditionalFundAllowedDisabled
            || specificObjectivesRemoved
            || strategiesRemoved
            || fundsRemoved
            || stateAidsRemoved
        )
            throw UpdateRestrictedFieldsWhenCallPublished()

        if (call.endDate.toInstant() != oldCall.endDate.toInstant() && call.endDate.isBefore(ZonedDateTime.now()))
            throw UpdatingEndDateIntoPast()
    }

    private fun List<ProgrammePriority>.mergeAllSpecificObjectives(): Set<ProgrammeObjectivePolicy> =
        map { it.specificObjectives.map { it.programmeObjectivePolicy }.toSet() }
            .fold(emptySet()) { first, second -> first union second }
}
