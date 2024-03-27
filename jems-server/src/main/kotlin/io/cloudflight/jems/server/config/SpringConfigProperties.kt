package io.cloudflight.jems.server.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.util.unit.DataSize

@AutoConfiguration
@ConfigurationProperties(prefix = "spring.servlet.multipart")
class SpringConfigProperties {
    var maxFileSize = DataSize.ofMegabytes(100)
}

