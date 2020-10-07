package io.cloudflight.jems.server.programme.service.validator

import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatusWrapper
import io.cloudflight.jems.api.programme.validator.ProgrammeLegalStatusInputValidator
import io.cloudflight.jems.server.programme.service.ProgrammeLegalStatusServiceImpl
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class ProgrammeLegalStatusInputValidatorImpl : ProgrammeLegalStatusInputValidator {

    override fun isProgrammeLegalStatusFilledInCorrectly(
        legalStatus: InputProgrammeLegalStatusWrapper,
        context: ConstraintValidatorContext
    ): Boolean {
        if (legalStatus.toPersist.size <= ProgrammeLegalStatusServiceImpl.MAX_COUNT) {
            return true;
        }
        context.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate("programme.legal.status.wrong.size").addConstraintViolation()
        return false
    }
}
