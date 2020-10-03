package io.cloudflight.jems.server.service.validators

import io.cloudflight.jems.api.validators.UniqueEmailValidator
import io.cloudflight.jems.server.user.service.UserService
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class UniqueEmailValidatorImpl(private val userService: UserService) : UniqueEmailValidator {

    override fun isValid(email: String?, context: ConstraintValidatorContext?): Boolean {
        return email?.let { userService.findOneByEmail(it) } == null
    }
}
