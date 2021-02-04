package io.cloudflight.jems.server.programme.repository.language

import io.cloudflight.jems.server.programme.service.language.ProgrammeLanguagePersistence
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeLanguagePersistenceProvider(
    private val repository: ProgrammeLanguageRepository
) : ProgrammeLanguagePersistence {

    @Transactional(readOnly = true)
    override fun getLanguages(): List<ProgrammeLanguage> =
        repository.findTop40ByOrderByCode().toModel()

    @Transactional
    override fun updateLanguages(languages: List<ProgrammeLanguage>): List<ProgrammeLanguage> =
        repository.saveAll(languages.toEntity()).toModel()

}
