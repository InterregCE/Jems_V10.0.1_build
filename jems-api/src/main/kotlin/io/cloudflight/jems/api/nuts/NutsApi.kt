package io.cloudflight.jems.api.nuts

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.nuts.dto.OutputNutsMetadata
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Api("Nuts Import")
interface NutsApi {

    companion object {
        private const val ENDPOINT_API_NUTS = "/api/nuts"
    }

    @ApiOperation("Retrieve info about NUTS version in the system, if already downloaded")
    @GetMapping("$ENDPOINT_API_NUTS/metadata")
    fun getNutsMetadata(): OutputNutsMetadata?

    @ApiOperation("Perform download of new GISCO-regions data, if not yet downloaded")
    @PostMapping("$ENDPOINT_API_NUTS/download")
    fun downloadLatestNuts(): OutputNutsMetadata

    @ApiOperation("Retrieve all possible NUTS from the system")
    @GetMapping(ENDPOINT_API_NUTS)
    fun getNuts(): List<OutputNuts>

}
