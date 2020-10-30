package io.cloudflight.jems.api.project.dto.partner.cofinancing

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Constraint(validatedBy = [ProjectFundValidator::class])
annotation class InputProjectPartnerCoFinancingValidator(
    val message: String = "project.partner.coFinancing.percentage.invalid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ProjectFundValidator(private val projectPartnerFundValidator: ProjectPartnerCoFinancingValidator) :
    ConstraintValidator<InputProjectPartnerCoFinancingValidator, InputProjectPartnerCoFinancingWrapper> {
    override fun isValid(projectCoFinancing: InputProjectPartnerCoFinancingWrapper, context: ConstraintValidatorContext): Boolean {
        return projectPartnerFundValidator.isCoFinancingFilledInCorrectly(projectCoFinancing.finances, context)
    }
}

interface ProjectPartnerCoFinancingValidator {

    fun isCoFinancingFilledInCorrectly(finances: Set<InputProjectPartnerCoFinancing>, context: ConstraintValidatorContext): Boolean

}
