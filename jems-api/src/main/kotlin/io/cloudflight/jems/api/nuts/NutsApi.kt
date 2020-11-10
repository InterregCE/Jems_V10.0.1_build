package io.cloudflight.jems.api.nuts

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.nuts.dto.OutputNutsMetadata
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Api("Nuts Import")
@RequestMapping("/api/nuts")
interface NutsApi {

    @ApiOperation("Retrieve info about NUTS version in the system, if already downloaded")
    @GetMapping("/metadata")
    fun getNutsMetadata(): OutputNutsMetadata?

    @ApiOperation("Perform download of new GISCO-regions data, if not yet downloaded")
    @PostMapping("/download")
    fun downloadLatestNuts(): OutputNutsMetadata

    @ApiOperation("Retrieve all possible NUTS from the system")
    @GetMapping
    fun getNuts(): List<OutputNuts>

}
