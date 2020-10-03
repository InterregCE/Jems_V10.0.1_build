package io.cloudflight.jems.api.programme.validator

import io.cloudflight.jems.api.programme.dto.InputProgrammeFundWrapper
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Constraint(validatedBy = [ProgrammeFundValidator::class])
annotation class InputProgrammeFundValidator(
    val message: String = "programme.fund.when.id.not.new.and.vice.versa",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ProgrammeFundValidator(private val programmeFundValidator: ProgrammeFundInputValidator) :
    ConstraintValidator<InputProgrammeFundValidator, InputProgrammeFundWrapper> {
    override fun isValid(programmeFund: InputProgrammeFundWrapper, context: ConstraintValidatorContext): Boolean {
        return programmeFundValidator.isProgrammeFundFilledInCorrectly(programmeFund, context)
    }
}

interface ProgrammeFundInputValidator {

    fun isProgrammeFundFilledInCorrectly(fund: InputProgrammeFundWrapper, context: ConstraintValidatorContext): Boolean

}
