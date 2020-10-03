package io.cloudflight.jems.server.programme.service.validator

import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatus
import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatusWrapper
import org.springframework.stereotype.Component
import io.cloudflight.jems.api.programme.validator.ProgrammeLegalStatusInputValidator
import io.cloudflight.jems.server.programme.service.ProgrammeLegalStatusServiceImpl
import javax.validation.ConstraintValidatorContext

@Component
class ProgrammeLegalStatusInputValidatorImpl : ProgrammeLegalStatusInputValidator {

    override fun isProgrammeLegalStatusFilledInCorrectly(legalStatus: InputProgrammeLegalStatusWrapper, context: ConstraintValidatorContext): Boolean {
        if (legalStatus.statuses.size > ProgrammeLegalStatusServiceImpl.MAX_COUNT) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("programme.legal.status.wrong.size").addConstraintViolation()
            return false
        }
        return legalStatus.statuses.all { isLegalStatusInputValid(it) }
    }

    private fun isLegalStatusInputValid(legalStatus: InputProgrammeLegalStatus): Boolean {
        val status = legalStatus.description
        if (legalStatus.id != null)
            return status == null
        else
            return status != null && status.isNotBlank()
    }

}
