package io.cloudflight.jems.api.programme

import io.cloudflight.jems.api.programme.dto.InputProgrammeLanguage
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLanguage
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Programme Language")
@RequestMapping("/api/programmelanguage")
interface ProgrammeLanguageApi {

    @ApiOperation("Retrieve programme languages")
    @GetMapping
    fun get(): List<OutputProgrammeLanguage>

    @ApiOperation("Specify languages for this programme")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@Valid @RequestBody programmeLanguages: Collection<InputProgrammeLanguage>): List<OutputProgrammeLanguage>

}
