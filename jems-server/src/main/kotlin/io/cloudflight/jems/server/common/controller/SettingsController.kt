package io.cloudflight.jems.server.common.controller

import io.cloudflight.jems.api.common.SettingsApi
import io.cloudflight.jems.server.config.SpringConfigProperties
import org.springframework.web.bind.annotation.RestController

@RestController
class SettingsController(private var springConfigProperties: SpringConfigProperties) : SettingsApi {

    override fun getMaximumAllowedFileSize(): Int? {
        return springConfigProperties.maxFileSize.toMegabytes().toInt()
    }
}
