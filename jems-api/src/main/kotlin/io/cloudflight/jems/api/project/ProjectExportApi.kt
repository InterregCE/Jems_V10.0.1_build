package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Project")
@RequestMapping("/api/project/{projectId}/export")
interface ProjectExportApi {

    @ApiOperation("Export budget data to csv file")
    @GetMapping("budget")
    fun exportBudget(
        @PathVariable projectId: Long,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam inputLanguage: SystemLanguage,
        @RequestParam(required = false) version: String? = null
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Export application form data to pdf file")
    @GetMapping("application")
    fun exportApplicationForm(
        @PathVariable projectId: Long,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam inputLanguage: SystemLanguage,
        @RequestParam(required = false) version: String? = null
    ): ResponseEntity<ByteArrayResource>
}
