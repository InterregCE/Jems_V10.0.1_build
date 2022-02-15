package io.cloudflight.jems.api.programme.language

import io.cloudflight.jems.api.programme.dto.language.ProgrammeLanguageDTO
import io.cloudflight.jems.api.programme.dto.language.AvailableProgrammeLanguagesDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Programme Language")
interface ProgrammeLanguageApi {

    companion object {
        private const val ENDPOINT_API_PROGRAMME_LANGUAGE = "/api/programmeLanguage"
    }

    @ApiOperation("Retrieve programme languages")
    @GetMapping("$ENDPOINT_API_PROGRAMME_LANGUAGE/available")
    fun getAvailableProgrammeLanguages(): AvailableProgrammeLanguagesDTO

    @ApiOperation("Retrieve programme languages")
    @GetMapping(ENDPOINT_API_PROGRAMME_LANGUAGE)
    fun get(): List<ProgrammeLanguageDTO>

    @ApiOperation("Specify languages for this programme")
    @PutMapping(ENDPOINT_API_PROGRAMME_LANGUAGE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@RequestBody programmeLanguages: Collection<ProgrammeLanguageDTO>): List<ProgrammeLanguageDTO>

}
