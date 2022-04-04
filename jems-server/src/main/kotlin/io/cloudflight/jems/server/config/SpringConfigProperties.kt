package io.cloudflight.jems.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.util.unit.DataSize

@Configuration
@ConfigurationProperties(prefix = "spring.servlet.multipart")
class SpringConfigProperties {
    var maxFileSize = DataSize.ofMegabytes(100)
}

