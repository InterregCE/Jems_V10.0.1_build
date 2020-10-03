package io.cloudflight.jems.server.programme.service.validator

import io.cloudflight.jems.api.programme.dto.InputProgrammeFund
import io.cloudflight.jems.api.programme.dto.InputProgrammeFundWrapper
import io.cloudflight.jems.api.programme.validator.ProgrammeFundInputValidator
import io.cloudflight.jems.server.programme.service.ProgrammeFundServiceImpl.Companion.MAX_COUNT
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class ProgrammeFundInputValidatorImpl : ProgrammeFundInputValidator {

    override fun isProgrammeFundFilledInCorrectly(fund: InputProgrammeFundWrapper, context: ConstraintValidatorContext): Boolean {
        if (fund.funds.size > MAX_COUNT) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("programme.fund.wrong.size").addConstraintViolation()
            return false
        }
        return fund.funds.all { isFundInputValid(it) }
    }

    private fun isFundInputValid(fund: InputProgrammeFund): Boolean {
        val abbreviation = fund.abbreviation
        val description = fund.description
        if (fund.id != null)
            return abbreviation == null && description == null
        else
            return abbreviation != null && abbreviation.isNotBlank()

    }

}
