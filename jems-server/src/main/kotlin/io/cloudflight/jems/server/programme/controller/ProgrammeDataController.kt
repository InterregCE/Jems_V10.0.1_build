package io.cloudflight.jems.server.programme.controller

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.programme.ProgrammeDataApi
import io.cloudflight.jems.api.programme.dto.InputProgrammeData
import io.cloudflight.jems.api.programme.dto.OutputProgrammeData
import io.cloudflight.jems.server.call.service.CallService
import io.cloudflight.jems.server.programme.service.ProgrammeDataService
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeDataController(
    private val programmeDataService: ProgrammeDataService,
    private val callService: CallService,
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

    override fun getNuts(): List<OutputNuts> = programmeDataService.getAvailableNuts()

    override fun isProgrammeSetupLocked(): Boolean = callService.existsPublishedCall()

}
