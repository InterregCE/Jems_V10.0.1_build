package io.cloudflight.ems.programme.service.validator

import io.cloudflight.ems.api.programme.dto.InputProgrammeLegalStatus
import io.cloudflight.ems.api.programme.dto.InputProgrammeLegalStatusWrapper
import org.springframework.stereotype.Component
import io.cloudflight.ems.api.programme.validator.ProgrammeLegalStatusInputValidator
import io.cloudflight.ems.programme.service.ProgrammeLegalStatusServiceImpl
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
