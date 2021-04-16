package io.cloudflight.jems.server.user.service.userrole

import io.cloudflight.jems.server.common.validator.GeneralValidatorService

fun validateUserRoleCommon(generalValidator: GeneralValidatorService, name: String) {
    generalValidator.throwIfAnyIsInvalid(
        generalValidator.notBlank(name, "name"),
        generalValidator.maxLength(name, 127, "name"),
    )
}
