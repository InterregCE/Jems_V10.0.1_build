package io.cloudflight.jems.api.programme.validator

import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityCreate
import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityPolicy
import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityUpdate
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Constraint(validatedBy = [PriorityPolicyUniqueValidator::class])
annotation class PriorityPoliciesHaveUniqueCodes(
    val message: String = "programme.priority.priorityPolicies.code.already.in.use",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class PriorityPolicyUniqueValidator(private val policySelectionValidator: PriorityPolicyUniqueCodeValidator) :
    ConstraintValidator<PriorityPoliciesHaveUniqueCodes, Any> {
    override fun isValid(programmePriority: Any?, context: ConstraintValidatorContext?): Boolean {

        var policies: Set<InputProgrammePriorityPolicy>? = null
        var priorityId: Long? = null

        if (programmePriority is InputProgrammePriorityCreate)
            policies = programmePriority.programmePriorityPolicies ?: return true
        if (programmePriority is InputProgrammePriorityUpdate) {
            policies = programmePriority.programmePriorityPolicies ?: return true
            priorityId = programmePriority.id
        }

        if (policies == null) return true

        val areAllPoliciesFreeOrBelongsToThisProgramme = policies.all {
            policySelectionValidator.isPolicyFreeOrBelongsToThisProgramme(it.programmeObjectivePolicy!!, priorityId)
        }

        if (!areAllPoliciesFreeOrBelongsToThisProgramme)
            return false

        return policies.all { policySelectionValidator.isPolicyCodeUniqueOrNotChanged(it) }
    }
}

interface PriorityPolicyUniqueCodeValidator {

    fun isPolicyFreeOrBelongsToThisProgramme(policy: ProgrammeObjectivePolicy, priorityId: Long? = null): Boolean

    fun isPolicyCodeUniqueOrNotChanged(policyData: InputProgrammePriorityPolicy): Boolean

}
