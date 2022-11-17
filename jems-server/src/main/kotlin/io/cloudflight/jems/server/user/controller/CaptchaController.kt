package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.user.CaptchaApi
import io.cloudflight.jems.api.user.dto.CaptchaDTO
import io.cloudflight.jems.server.user.service.user.register_user.RegisterUserInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class CaptchaController(
    val registerUserInteractor: RegisterUserInteractor,
) :CaptchaApi {
    override fun getCaptcha(): CaptchaDTO {
       return registerUserInteractor.getCaptcha().toDto()
    }
}
