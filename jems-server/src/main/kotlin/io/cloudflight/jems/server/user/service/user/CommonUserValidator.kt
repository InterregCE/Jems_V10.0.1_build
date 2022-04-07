package io.cloudflight.jems.server.user.service.user

import io.cloudflight.jems.server.common.validator.EMAIL_REGEX
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.common.validator.PASSWORD_REGEX
import io.cloudflight.jems.server.user.service.model.UserChange

const val PASSWORD_FIELD_NAME = "password"
const val PASSWORD_ERROR_KEY = "user.password.constraints.not.satisfied"

fun validateUserCommon(generalValidator: GeneralValidatorService, user: UserChange) {
    generalValidator.throwIfAnyIsInvalid(
        generalValidator.notBlank(user.email, "email"),
        generalValidator.matches(user.email, EMAIL_REGEX, "email", "user.email.wrong.format"),
        generalValidator.maxLength(user.email, 255, "email"),
        generalValidator.notBlank(user.name, "name"),
        generalValidator.maxLength(user.name, 50, "surname"),
        generalValidator.notBlank(user.surname, "surname"),
        generalValidator.maxLength(user.surname, 50, "name"),
    )
}

fun validatePassword(generalValidator: GeneralValidatorService, password: String) {
    generalValidator.throwIfAnyIsInvalid(
        generalValidator.matches(
            password,
            PASSWORD_REGEX,
            PASSWORD_FIELD_NAME,
            PASSWORD_ERROR_KEY,
        )
    )
}
