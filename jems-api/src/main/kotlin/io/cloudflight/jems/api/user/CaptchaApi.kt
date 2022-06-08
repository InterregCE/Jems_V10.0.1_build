package io.cloudflight.jems.api.user

import io.cloudflight.jems.api.user.dto.CaptchaDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping

@Api("Captcha Api")
interface CaptchaApi {

    companion object {
        private const val ENDPOINT_API_CAPTCHA = "/api/captcha"
    }

    @ApiOperation("Gets the captcha data")
    @GetMapping(ENDPOINT_API_CAPTCHA)
    fun getCaptcha(): CaptchaDTO
}
