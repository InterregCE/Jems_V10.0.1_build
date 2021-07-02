package io.cloudflight.jems.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties("app")
class AppResourcesProperties {
    var translationsFolder = "resources/translations"
}