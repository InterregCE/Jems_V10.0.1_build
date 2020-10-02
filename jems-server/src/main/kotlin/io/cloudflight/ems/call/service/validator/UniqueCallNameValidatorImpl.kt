package io.cloudflight.ems.call.service.validator

import io.cloudflight.ems.api.call.validator.UniqueCallNameValidator
import io.cloudflight.ems.call.service.CallService
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class UniqueCallNameValidatorImpl(private val callService: CallService) : UniqueCallNameValidator {

    override fun isValid(name: String?): Boolean {
        return name?.let { callService.findOneByName(it) } == null
    }

}
