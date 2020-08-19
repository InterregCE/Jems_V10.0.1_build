package io.cloudflight.ems.api.indicator.validator

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [UniqueOutputIndicator::class])
annotation class UniqueIndicatorOutputIdentifier(
    val message: String = "indicator.identifier.already.in.use",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [UniqueResultIndicator::class])
annotation class UniqueIndicatorResultIdentifier(
    val message: String = "indicator.identifier.already.in.use",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class UniqueOutputIndicator(private val uniqueIndicatorIdentifier: UniqueIndicatorIdentifierValidator) :
    ConstraintValidator<UniqueIndicatorOutputIdentifier, String> {
    override fun isValid(identifier: String?, context: ConstraintValidatorContext?): Boolean {
        if (identifier != null)
            return uniqueIndicatorIdentifier.isUniqueForOutput(identifier)
        return true
    }
}

class UniqueResultIndicator(private val uniqueIndicatorIdentifier: UniqueIndicatorIdentifierValidator) :
    ConstraintValidator<UniqueIndicatorResultIdentifier, String> {
    override fun isValid(identifier: String?, context: ConstraintValidatorContext?): Boolean {
        if (identifier != null)
            return uniqueIndicatorIdentifier.isUniqueForResult(identifier)
        return true
    }
}

interface UniqueIndicatorIdentifierValidator {

    fun isUniqueForOutput(identifier: String): Boolean

    fun isUniqueForResult(identifier: String): Boolean

}
