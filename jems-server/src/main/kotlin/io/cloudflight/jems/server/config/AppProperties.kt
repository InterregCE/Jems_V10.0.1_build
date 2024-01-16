package io.cloudflight.jems.server.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties


const val DEFAULT_SERVER_URL = "http://localhost:4200"

@AutoConfiguration
@ConfigurationProperties("app")
class AppProperties {
    var translationsFolder = "resources/translations"
    var serverUrl: String = DEFAULT_SERVER_URL
}
