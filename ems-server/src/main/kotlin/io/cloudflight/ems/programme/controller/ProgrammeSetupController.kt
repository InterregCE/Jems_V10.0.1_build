package io.cloudflight.ems.programme.controller

import io.cloudflight.ems.api.programme.ProgrammeSetupApi
import io.cloudflight.ems.api.programme.dto.InputProgrammeData
import io.cloudflight.ems.programme.service.ProgrammeDataService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
class ProgrammeSetupController(
    private val programmeDataService: ProgrammeDataService
) : ProgrammeSetupApi {

    override fun get(): InputProgrammeData {
        return programmeDataService.get()
    }

    override fun update(dataInput: InputProgrammeData): InputProgrammeData {
        return programmeDataService.update(dataInput)
    }

}
