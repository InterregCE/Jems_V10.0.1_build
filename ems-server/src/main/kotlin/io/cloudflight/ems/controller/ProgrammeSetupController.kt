package io.cloudflight.ems.controller

import io.cloudflight.ems.api.ProgrammeSetupApi
import io.cloudflight.ems.api.dto.ProgrammeSetup
import io.cloudflight.ems.service.ProgrammeDataService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeSetupController(
    private val programmeDataService: ProgrammeDataService
) : ProgrammeSetupApi {

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun get(): ProgrammeSetup {
        return programmeDataService.get()
    }

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun update(setup: ProgrammeSetup): ProgrammeSetup {
        return programmeDataService.update(setup)
    }

}
