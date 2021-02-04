package io.cloudflight.jems.server.programme.service.language

import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage

interface ProgrammeLanguagePersistence {
    fun getLanguages(): List<ProgrammeLanguage>
    fun updateLanguages(languages: List<ProgrammeLanguage>): List<ProgrammeLanguage>
}
