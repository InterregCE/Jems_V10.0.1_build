package io.cloudflight.ems.api

import io.cloudflight.ems.api.dto.ProgrammeSetup
import io.swagger.annotations.Api
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Programme Setup")
@RequestMapping("/api/setup")
interface ProgrammeSetupApi {

    @GetMapping
    fun get(): ProgrammeSetup

    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@Valid @RequestBody setup: ProgrammeSetup): ProgrammeSetup

}
