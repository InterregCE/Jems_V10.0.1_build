package io.cloudflight.jems.server.programme.controller

import io.cloudflight.jems.api.programme.ProgrammeDataApi
import io.cloudflight.jems.api.programme.dto.InputProgrammeData
import io.cloudflight.jems.api.programme.dto.OutputProgrammeData
import io.cloudflight.jems.server.programme.service.ProgrammeDataService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
class ProgrammeDataController(
    private val programmeDataService: ProgrammeDataService
) : ProgrammeDataApi {

    override fun get(): OutputProgrammeData {
        return programmeDataService.get()
    }

    override fun update(programmeData: InputProgrammeData): OutputProgrammeData {
        return programmeDataService.update(programmeData)
    }

    override fun updateNuts(regions: Collection<String>): OutputProgrammeData {
        return programmeDataService.saveProgrammeNuts(regions)
    }

}
