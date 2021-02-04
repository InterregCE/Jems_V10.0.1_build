package io.cloudflight.jems.server.programme.service.language.update_languages

import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage

interface UpdateLanguagesInteractor {

    fun updateLanguages(languages: List<ProgrammeLanguage>): List<ProgrammeLanguage>

}
