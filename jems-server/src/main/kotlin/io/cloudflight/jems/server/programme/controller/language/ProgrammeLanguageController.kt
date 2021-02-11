package io.cloudflight.jems.server.programme.controller.language

import io.cloudflight.jems.api.programme.language.ProgrammeLanguageApi
import io.cloudflight.jems.api.programme.dto.language.ProgrammeLanguageDTO
import io.cloudflight.jems.api.programme.dto.language.AvailableProgrammeLanguagesDTO
import io.cloudflight.jems.server.programme.service.language.get_languages.GetLanguagesInteractor
import io.cloudflight.jems.server.programme.service.language.update_languages.UpdateLanguagesInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeLanguageController(
    private val getLanguageInteractor: GetLanguagesInteractor,
    private val updateLanguageInteractor: UpdateLanguagesInteractor,
) : ProgrammeLanguageApi {

    override fun getAvailableProgrammeLanguages(): AvailableProgrammeLanguagesDTO =
        getLanguageInteractor.getAvailableLanguages().toDto()

    override fun get(): List<ProgrammeLanguageDTO> =
        getLanguageInteractor.getAllLanguages().toDto()

    override fun update(programmeLanguages: Collection<ProgrammeLanguageDTO>): List<ProgrammeLanguageDTO> =
        updateLanguageInteractor.updateLanguages(programmeLanguages.toModel()).toDto()

}
