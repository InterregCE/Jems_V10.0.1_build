package io.cloudflight.ems.service.call.validator

import io.cloudflight.ems.api.call.validator.UniqueCallNameValidator
import io.cloudflight.ems.service.call.CallService
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class UniqueCallNameValidatorImpl(private val callService: CallService) : UniqueCallNameValidator {

    override fun isValid(name: String?, context: ConstraintValidatorContext?): Boolean {
        return name?.let { callService.findOneByName(it) } == null
    }

}
