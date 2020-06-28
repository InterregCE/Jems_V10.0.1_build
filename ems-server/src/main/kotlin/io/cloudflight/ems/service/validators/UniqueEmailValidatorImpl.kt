package io.cloudflight.ems.service.validators

import io.cloudflight.ems.api.validators.UniqueEmailValidator
import io.cloudflight.ems.service.UserService
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class UniqueEmailValidatorImpl(private val userService: UserService) : UniqueEmailValidator {

    override fun isValid(email: String?, context: ConstraintValidatorContext?): Boolean {
        return email?.let { userService.findOneByEmail(it) } == null
    }
}
