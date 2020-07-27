package io.cloudflight.ems.api.validators

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [EmailValidator::class])
annotation class UniqueUserEmail(
    val message: String = "user.email.not.unique",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class EmailValidator(private val uniqueEmailValidator: UniqueEmailValidator) :
    ConstraintValidator<UniqueUserEmail, String> {
    override fun isValid(email: String?, context: ConstraintValidatorContext?): Boolean {
        return uniqueEmailValidator.isValid(email, context)
    }
}

interface UniqueEmailValidator {
    fun isValid(email: String?, context: ConstraintValidatorContext?): Boolean
}
