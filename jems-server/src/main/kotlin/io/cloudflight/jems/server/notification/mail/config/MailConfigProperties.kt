package io.cloudflight.jems.server.notification.mail.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

const val DEFAULT_SENDER = "jems@jems.com"
const val MAIL_CONFIG_PROPERTIES_PREFIX = "app.notification.mail"
const val MAIL_ENABLED = "enabled"

@Configuration
@ConfigurationProperties(prefix = MAIL_CONFIG_PROPERTIES_PREFIX)
class MailConfigProperties {
    var enabled: Boolean = true
    var bccList: List<String> = emptyList()
    var sender: String = DEFAULT_SENDER
}
