package io.cloudflight.jems.server.programme.controller

import io.cloudflight.jems.api.programme.ProgrammeDataApi
import io.cloudflight.jems.api.programme.dto.InputProgrammeData
import io.cloudflight.jems.api.programme.dto.OutputProgrammeData
import io.cloudflight.jems.server.programme.service.ProgrammeDataService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeDataController(
    private val programmeDataService: ProgrammeDataService
) : ProgrammeDataApi {

    /**
     * @PreAuthorize is missing as parts of programmeData (f.e. languages) have to be available before login
     */
    override fun get(): OutputProgrammeData {
        return programmeDataService.get()
    }

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun update(programmeData: InputProgrammeData): OutputProgrammeData {
        return programmeDataService.update(programmeData)
    }

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun updateNuts(regions: Collection<String>): OutputProgrammeData {
        return programmeDataService.saveProgrammeNuts(regions)
    }

}
