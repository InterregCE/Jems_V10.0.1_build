package io.cloudflight.jems.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.security")
class AppSecurityProperties {
    var defaultPasswordPrefix: String = "Jems@2020"
}
