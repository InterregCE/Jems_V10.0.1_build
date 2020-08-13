package io.cloudflight.ems.nuts.controller

import io.cloudflight.ems.api.nuts.ProgrammeNutsApi
import io.cloudflight.ems.nuts.service.ProgrammeNutsService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeNutsController(
    val programmeNutsService: ProgrammeNutsService
) : ProgrammeNutsApi {

    override fun getProgrammeNuts(): Any {
        return programmeNutsService.getProgrammeNuts()
    }

    @PreAuthorize("@programmeNutsAuthorization.canSetupNuts()")
    override fun updateProgrammeNuts(regions: Collection<String>): Any {
        return programmeNutsService.saveProgrammeNuts(regions)
    }

}
