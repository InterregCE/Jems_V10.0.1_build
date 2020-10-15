package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeLanguage
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLanguage

interface ProgrammeLanguageService {

    fun get(): List<OutputProgrammeLanguage>

    fun update(programmeLanguages: Collection<InputProgrammeLanguage>): List<OutputProgrammeLanguage>

}
