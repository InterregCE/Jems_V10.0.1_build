package io.cloudflight.ems.programme.service.validator

import io.cloudflight.ems.api.programme.dto.InputProgrammePriorityPolicy
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective
import io.cloudflight.ems.api.programme.validator.PolicyObjectiveRelationshipValidator
import org.springframework.stereotype.Component

@Component
class PolicyObjectiveRelationshipValidatorImpl : PolicyObjectiveRelationshipValidator {

    override fun isValid(policies: Set<InputProgrammePriorityPolicy>?, objective: ProgrammeObjective): Boolean {
        return policies?.all {
            it.programmeObjectivePolicy?.objective == objective
        } ?: return true
    }

}
