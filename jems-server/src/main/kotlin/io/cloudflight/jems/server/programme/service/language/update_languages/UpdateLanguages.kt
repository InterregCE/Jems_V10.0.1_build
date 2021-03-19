package io.cloudflight.jems.server.programme.service.language.update_languages

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.language.ProgrammeLanguagePersistence
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import io.cloudflight.jems.server.programme.service.programmeInputLanguagesChanged
import io.cloudflight.jems.server.programme.service.programmeUILanguagesChanged
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLanguages(
    private val persistence: ProgrammeLanguagePersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val auditService: AuditService,
) : UpdateLanguagesInteractor {

    companion object {
        const val MAX_ALLOWED_SYSTEM = 40
        const val MIN_ALLOWED_INPUT = 1
        const val MAX_ALLOWED_INPUT = 4
    }

    @CanUpdateProgrammeSetup
    @Transactional
    override fun updateLanguages(languages: List<ProgrammeLanguage>): List<ProgrammeLanguage> {
        if (isProgrammeSetupLocked.isLocked())
            throw UpdateLanguagesWhenProgrammeSetupRestricted()
        validateLanguageRequirements(languages)

        val result = persistence.updateLanguages(languages)
        programmeUILanguagesChanged(result).logWith(auditService)
        programmeInputLanguagesChanged(result).logWith(auditService)
        return result
    }

    private fun validateLanguageRequirements(languages: List<ProgrammeLanguage>) {
        if (languages.size > MAX_ALLOWED_SYSTEM)
            throwUnprocessableEntity("programme.language.max.allowed.reached")

        val inputLanguagesCount = languages.count { it.input }

        if (inputLanguagesCount < MIN_ALLOWED_INPUT)
            throwUnprocessableEntity("programme.language.min.allowed.input.languages")

        if (inputLanguagesCount > MAX_ALLOWED_INPUT)
            throwUnprocessableEntity("programme.language.max.allowed.input.languages")
    }

    private fun throwUnprocessableEntity(message: String) {
        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = message
        )
    }

}
