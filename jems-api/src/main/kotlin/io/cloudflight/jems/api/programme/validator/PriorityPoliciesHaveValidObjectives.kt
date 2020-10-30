package io.cloudflight.jems.api.programme.validator

import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityCreate
import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityPolicy
import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityUpdate
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Constraint(validatedBy = [PolicyObjectiveValidator::class])
annotation class PriorityPoliciesHaveValidObjectives(
    val message: String = "programme.priority.priorityPolicies.should.not.be.of.different.objectives",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class PolicyObjectiveValidator(private val policyObjectiveRelationshipValidator: PolicyObjectiveRelationshipValidator) :
    ConstraintValidator<PriorityPoliciesHaveValidObjectives, Any> {
    override fun isValid(programmePriority: Any?, context: ConstraintValidatorContext?): Boolean {
        if (programmePriority is InputProgrammePriorityCreate)
            return policyObjectiveRelationshipValidator
                .isValid(programmePriority.programmePriorityPolicies, programmePriority.objective!!)
        if (programmePriority is InputProgrammePriorityUpdate)
            return policyObjectiveRelationshipValidator
                .isValid(programmePriority.programmePriorityPolicies, programmePriority.objective!!)

        throw IllegalArgumentException("This validator is supposed to work only with InputProgrammePriority DTOs")
    }
}

interface PolicyObjectiveRelationshipValidator {

    /**
     * Validate, if every objective policy is properly set to correct objective.
     */
    fun isValid(policies: Set<InputProgrammePriorityPolicy>?, objective: ProgrammeObjective): Boolean

}
