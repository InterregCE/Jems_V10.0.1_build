package io.cloudflight.jems.server.call.service.validator

import io.cloudflight.jems.api.call.validator.UniqueCallNameValidator
import io.cloudflight.jems.server.call.service.CallService
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class UniqueCallNameValidatorImpl(private val callService: CallService) : UniqueCallNameValidator {

    override fun isValid(name: String?): Boolean {
        return name?.let { callService.findOneByName(it) } == null
    }

}
