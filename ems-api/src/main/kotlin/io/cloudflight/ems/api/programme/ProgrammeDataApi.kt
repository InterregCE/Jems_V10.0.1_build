package io.cloudflight.ems.api.programme

import io.cloudflight.ems.api.programme.dto.ProgrammeBasicData
import io.swagger.annotations.Api
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Programme Data")
@RequestMapping("/api/programmedata")
interface ProgrammeDataApi {

    @GetMapping
    fun get(): ProgrammeBasicData

    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@Valid @RequestBody basicData: ProgrammeBasicData): ProgrammeBasicData

}
