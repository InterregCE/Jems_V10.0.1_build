package io.cloudflight.jems.server.nuts.controller

import io.cloudflight.jems.api.nuts.NutsApi
import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.nuts.dto.OutputNutsMetadata
import io.cloudflight.jems.server.nuts.service.NutsService
import org.springframework.web.bind.annotation.RestController

@RestController
class NutsController(
    val nutsService: NutsService
) : NutsApi {

    override fun getNutsMetadata(): OutputNutsMetadata? {
        return nutsService.getNutsMetadata()
    }

    override fun downloadLatestNuts(): OutputNutsMetadata {
        return nutsService.downloadLatestNutsFromGisco()
    }

    override fun getNuts(): List<OutputNuts> {
        return nutsService.getNuts()
    }

}
