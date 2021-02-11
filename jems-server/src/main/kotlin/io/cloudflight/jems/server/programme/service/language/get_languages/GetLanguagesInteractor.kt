package io.cloudflight.jems.server.programme.service.language.get_languages

import io.cloudflight.jems.server.programme.service.language.model.AvailableProgrammeLanguages
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage

interface GetLanguagesInteractor {

    fun getAvailableLanguages(): AvailableProgrammeLanguages

    fun getAllLanguages(): List<ProgrammeLanguage>

}
