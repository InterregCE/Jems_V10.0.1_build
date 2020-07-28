package io.cloudflight.ems.programme.controller

import io.cloudflight.ems.api.programme.ProgrammeSetupApi
import io.cloudflight.ems.api.dto.ProgrammeSetup
import io.cloudflight.ems.service.ProgrammeDataService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
class ProgrammeSetupController(
    private val programmeDataService: ProgrammeDataService
) : ProgrammeSetupApi {

    override fun get(): ProgrammeSetup {
        return programmeDataService.get()
    }

    override fun update(setup: ProgrammeSetup): ProgrammeSetup {
        return programmeDataService.update(setup)
    }

}
