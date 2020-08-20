package io.cloudflight.ems.nuts.controller

import io.cloudflight.ems.api.nuts.NutsApi
import io.cloudflight.ems.api.nuts.dto.OutputNutsMetadata
import io.cloudflight.ems.nuts.service.NutsService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController


@RestController
@PreAuthorize("@nutsAuthorization.canSetupNuts()")
class NutsController(
    val nutsService: NutsService
) : NutsApi {

    override fun getNutsMetadata(): OutputNutsMetadata? {
        return nutsService.getNutsMetadata()
    }

    override fun downloadLatestNuts(): OutputNutsMetadata {
        return nutsService.downloadLatestNutsFromGisco()
    }

    override fun getNuts(): Any {
        return nutsService.getNuts()
    }

}
