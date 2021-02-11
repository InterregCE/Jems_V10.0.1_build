package io.cloudflight.jems.server.programme.service.language.get_languages

import io.cloudflight.jems.server.programme.authorization.CanReadProgrammeSetup
import io.cloudflight.jems.server.programme.service.language.ProgrammeLanguagePersistence
import io.cloudflight.jems.server.programme.service.language.model.AvailableProgrammeLanguages
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetLanguages(
    private val persistence: ProgrammeLanguagePersistence,
) : GetLanguagesInteractor {

    @Transactional(readOnly = true)
    override fun getAvailableLanguages(): AvailableProgrammeLanguages {
        val languages = persistence.getLanguages().filter { it.input || it.ui || it.fallback }
        return AvailableProgrammeLanguages(
            inputLanguages = languages.filter { it.input }.mapTo(HashSet()) { it.code },
            systemLanguages = languages.filter { it.ui }.mapTo(HashSet()) { it.code },
            fallbackLanguage = languages.first { it.fallback }.code,
        )
    }

    @CanReadProgrammeSetup
    @Transactional(readOnly = true)
    override fun getAllLanguages(): List<ProgrammeLanguage> =
        persistence.getLanguages()

}
