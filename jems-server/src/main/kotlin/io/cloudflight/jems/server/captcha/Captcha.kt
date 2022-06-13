package io.cloudflight.jems.server.captcha

data class Captcha(
    val captcha: String,
    val hiddenCaptcha: String,
    val realCaptcha: String
)
