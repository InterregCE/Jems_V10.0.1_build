package io.cloudflight.jems.server.common.controller

import io.cloudflight.jems.api.common.InfoApi
import io.cloudflight.jems.api.common.dto.VersionDTO
import io.cloudflight.platform.server.ServerModuleIdentification
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.web.bind.annotation.RestController

@RestController
class InfoController(
    private val serverModuleIdentification: ServerModuleIdentification,
    private val infoEndpoint: InfoEndpoint
) : InfoApi {

    override fun getVersionInfo(): VersionDTO = VersionDTO(
        version = serverModuleIdentification.getVersion(),
        commitId = serverModuleIdentification.getId(),
        helpdeskUrl = infoEndpoint.info()["helpdesk-url"]?.toString() ?: "",
        accessibilityStatementUrl = infoEndpoint.info()["accessibility-statement-url"]?.toString() ?: "",
    )

}
