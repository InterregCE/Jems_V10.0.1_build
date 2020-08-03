package io.cloudflight.ems.api.call.validator

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [CallNameValidator::class])
annotation class UniqueCallName(
    val message: String = "call.name.already.in.use",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class CallNameValidator(private val uniqueNameValidator: UniqueCallNameValidator) :
    ConstraintValidator<UniqueCallName, String> {
    override fun isValid(name: String?, context: ConstraintValidatorContext?): Boolean {
        return uniqueNameValidator.isValid(name)
    }
}

interface UniqueCallNameValidator {
    fun isValid(name: String?): Boolean
}
