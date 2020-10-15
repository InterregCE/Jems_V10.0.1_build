package io.cloudflight.jems.server.programme.controller

import io.cloudflight.jems.api.programme.ProgrammeLanguageApi
import io.cloudflight.jems.api.programme.dto.InputProgrammeLanguage
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLanguage
import io.cloudflight.jems.server.programme.service.ProgrammeLanguageService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeLanguageController(
    private val programmeLanguageService: ProgrammeLanguageService
) : ProgrammeLanguageApi {

    /**
     * @PreAuthorize is missing as languages have to be available before login
     */
    override fun get(): List<OutputProgrammeLanguage> {
        return programmeLanguageService.get()
    }

    @PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
    override fun update(programmeLanguages: Collection<InputProgrammeLanguage>): List<OutputProgrammeLanguage> {
        return programmeLanguageService.update(programmeLanguages)
    }

}
