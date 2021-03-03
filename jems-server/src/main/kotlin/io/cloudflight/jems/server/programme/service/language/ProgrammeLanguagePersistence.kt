package io.cloudflight.jems.server.programme.service.language

import io.cloudflight.jems.server.programme.service.ProgrammePersistence
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage

interface ProgrammeLanguagePersistence: ProgrammePersistence {
    fun getLanguages(): List<ProgrammeLanguage>
    fun updateLanguages(languages: List<ProgrammeLanguage>): List<ProgrammeLanguage>
}
