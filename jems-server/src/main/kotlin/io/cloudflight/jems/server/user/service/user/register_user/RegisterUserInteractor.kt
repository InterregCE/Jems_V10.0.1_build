package io.cloudflight.jems.server.user.service.user.register_user

import io.cloudflight.jems.server.captcha.Captcha
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRegistration

interface RegisterUserInteractor {

    fun registerUser(user: UserRegistration): User

    fun getCaptcha(): Captcha

}
