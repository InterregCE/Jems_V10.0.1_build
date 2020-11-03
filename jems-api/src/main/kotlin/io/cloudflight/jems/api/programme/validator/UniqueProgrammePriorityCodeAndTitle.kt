package io.cloudflight.jems.api.programme.validator

import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityCreate
import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityUpdate
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Constraint(validatedBy = [PriorityCodeAndTitleValidator::class])
annotation class UniqueProgrammePriorityCodeAndTitle(
    val message: String = "programme.priority.code.or.title.already.in.use",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class PriorityCodeAndTitleValidator(private val uniqueProgrammePriorityCodeAndTitleValidator: UniqueProgrammePriorityCodeAndTitleValidator) :
    ConstraintValidator<UniqueProgrammePriorityCodeAndTitle, Any> {
    override fun isValid(programmePriority: Any?, context: ConstraintValidatorContext?): Boolean {
        if (programmePriority is InputProgrammePriorityCreate)
            return uniqueProgrammePriorityCodeAndTitleValidator
                .isValid(
                    null,
                    programmePriority.code!!,
                    programmePriority.title!!
                )
        if (programmePriority is InputProgrammePriorityUpdate)
            return uniqueProgrammePriorityCodeAndTitleValidator
                .isValid(
                    programmePriority.id,
                    programmePriority.code!!,
                    programmePriority.title!!
                )
        throw IllegalArgumentException("This validator is supposed to work only with InputProgrammePriority DTOs")
    }
}

interface UniqueProgrammePriorityCodeAndTitleValidator {

    fun isValid(id: Long?, code: String, title: String): Boolean

}
