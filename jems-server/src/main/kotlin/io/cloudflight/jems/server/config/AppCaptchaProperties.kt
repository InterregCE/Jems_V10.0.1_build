package io.cloudflight.jems.server.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties

@AutoConfiguration
@ConfigurationProperties(prefix = "app.captcha")
class AppCaptchaProperties {
   var enabled: Boolean = true
}
