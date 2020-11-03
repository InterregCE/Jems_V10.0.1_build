package io.cloudflight.jems.server.programme.service.validator

import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityPolicy
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimple
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.validator.PriorityPolicyUniqueCodeValidator
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.ProgrammePriorityService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class PriorityPolicyUniqueCodeValidatorImpl(
    private val programmePriorityService: ProgrammePriorityService
) : PriorityPolicyUniqueCodeValidator {

    override fun isPolicyFreeOrBelongsToThisProgramme(policy: ProgrammeObjectivePolicy, priorityId: Long?): Boolean {
        val priorityIdOfRelatedExistingPolicy = programmePriorityService.getPriorityIdForPolicyIfExists(policy)
        if (priorityId == null) {
            // creating new ProgrammePriority
            if (priorityIdOfRelatedExistingPolicy == null)
                return true
            else
                throw buildPolicyInUseException(policy)
        } else {
            // updating existing ProgrammePriority
            if (priorityIdOfRelatedExistingPolicy == null || priorityIdOfRelatedExistingPolicy == priorityId)
                return true
            else
                throw buildPolicyInUseException(policy)
        }
    }

    private fun buildPolicyInUseException(policy: ProgrammeObjectivePolicy): I18nValidationException {
        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = mapOf("programme.priorityPolicy.programmeObjectivePolicy" to I18nFieldError(
                i18nKey = "programme.priorityPolicy.programmeObjectivePolicy.already.in.use",
                i18nArguments = listOf(policy.name)
            ))
        )
    }

    override fun isPolicyCodeUniqueOrNotChanged(policyData: InputProgrammePriorityPolicy): Boolean {
        val policyWithSuchCode = programmePriorityService.getPriorityPolicyByCode(policyData.code!!)
        if (policyWithSuchCode != null)
            // if code is already in use, it has to be used with this programme policy
            if (policyWithSuchCode.programmeObjectivePolicy == policyData.programmeObjectivePolicy)
                return true
            else
                throw buildPolicyCodeInUseException(policyWithSuchCode)
        return true
    }

    private fun buildPolicyCodeInUseException(policy: OutputProgrammePriorityPolicySimple): I18nValidationException {
        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = mapOf("programme.priorityPolicy.programmeObjectivePolicy" to I18nFieldError(
                i18nKey = "programme.priorityPolicy.programmeObjectivePolicy.code.already.in.use",
                i18nArguments = listOf(policy.code, policy.programmeObjectivePolicy.name)
            ))
        )
    }

}
