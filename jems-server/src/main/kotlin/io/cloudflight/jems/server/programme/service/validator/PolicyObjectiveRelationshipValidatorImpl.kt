package io.cloudflight.jems.server.programme.service.validator

import io.cloudflight.jems.api.programme.dto.InputProgrammePriorityPolicy
import io.cloudflight.jems.api.programme.dto.ProgrammeObjective
import io.cloudflight.jems.api.programme.validator.PolicyObjectiveRelationshipValidator
import org.springframework.stereotype.Component

@Component
class PolicyObjectiveRelationshipValidatorImpl : PolicyObjectiveRelationshipValidator {

    override fun isValid(policies: Set<InputProgrammePriorityPolicy>?, objective: ProgrammeObjective): Boolean {
        return policies?.all {
            it.programmeObjectivePolicy?.objective == objective
        } ?: return true
    }

}
