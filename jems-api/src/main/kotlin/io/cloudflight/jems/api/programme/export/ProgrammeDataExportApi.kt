package io.cloudflight.jems.api.programme.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Programme data export")
@RequestMapping("/api/programme/export")
interface ProgrammeDataExportApi {

    @ApiOperation("Export programme data to excel file")
    @GetMapping
    fun export(
        @RequestParam pluginKey: String?,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam inputLanguage: SystemLanguage
    ): ResponseEntity<ByteArrayResource>

}
