package io.cloudflight.jems.server.common.controller

import io.cloudflight.jems.api.common.InfoApi
import io.cloudflight.jems.api.common.dto.VersionDTO
import io.cloudflight.platform.server.ServerModuleIdentification
import org.springframework.web.bind.annotation.RestController

@RestController
class InfoController(
    private val serverModuleIdentification: ServerModuleIdentification,
) : InfoApi {

    override fun getVersionInfo(): VersionDTO = VersionDTO(
        version = serverModuleIdentification.getVersion(),
        commitId = serverModuleIdentification.getId(),
    )

}
