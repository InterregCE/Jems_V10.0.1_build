package io.cloudflight.jems.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


const val DEFAULT_SERVER_URL = "http://localhost:4200"

@Configuration
@ConfigurationProperties("app")
class AppProperties {
    var translationsFolder = "resources/translations"
    var serverUrl: String = DEFAULT_SERVER_URL
}
