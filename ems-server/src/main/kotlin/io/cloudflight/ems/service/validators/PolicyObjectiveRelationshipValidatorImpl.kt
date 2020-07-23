package io.cloudflight.ems.service.validators

import io.cloudflight.ems.api.programme.dto.InputProgrammePriorityCreate
import io.cloudflight.ems.api.programme.dto.InputProgrammePriorityPolicy
import io.cloudflight.ems.api.programme.dto.ProgrammeObjective
import io.cloudflight.ems.api.programme.validator.PolicyObjectiveRelationshipValidator
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class PolicyObjectiveRelationshipValidatorImpl : PolicyObjectiveRelationshipValidator {

    override fun isValid(policies: Set<InputProgrammePriorityPolicy>?, objective: ProgrammeObjective): Boolean {
        return policies?.all {
            it.programmeObjectivePolicy?.objective == objective
        } ?: return true
    }

}
