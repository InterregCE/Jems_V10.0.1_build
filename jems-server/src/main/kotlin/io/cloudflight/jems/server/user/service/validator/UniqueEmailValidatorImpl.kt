package io.cloudflight.jems.server.user.service.validator

import io.cloudflight.jems.api.user.validator.UniqueEmailValidator
import io.cloudflight.jems.server.user.service.UserService
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class UniqueEmailValidatorImpl(private val userService: UserService) : UniqueEmailValidator {

    override fun isValid(email: String?, context: ConstraintValidatorContext?): Boolean {
        return email?.let { userService.findOneByEmail(it) } == null
    }
}
