package io.cloudflight.jems.api.programme.validator

import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusUpdateDTO
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Constraint(validatedBy = [ProgrammeLegalStatusValidator::class])
annotation class InputProgrammeLegalStatusValidator(
    val message: String = "programme.legal.status.when.id.not.new.and.vice.versa",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ProgrammeLegalStatusValidator(private val programmeLegalStatusValidator: ProgrammeLegalStatusInputValidator) :
    ConstraintValidator<InputProgrammeLegalStatusValidator, ProgrammeLegalStatusUpdateDTO> {
    override fun isValid(legalStatus: ProgrammeLegalStatusUpdateDTO, context: ConstraintValidatorContext): Boolean {
        return programmeLegalStatusValidator.isProgrammeLegalStatusFilledInCorrectly(legalStatus, context)
    }
}

interface ProgrammeLegalStatusInputValidator {

    fun isProgrammeLegalStatusFilledInCorrectly(legalStatus: ProgrammeLegalStatusUpdateDTO, context: ConstraintValidatorContext): Boolean

}
