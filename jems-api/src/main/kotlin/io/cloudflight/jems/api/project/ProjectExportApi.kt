package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Api("Project")
interface ProjectExportApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_EXPORT = "/api/project/{projectId}/export"
    }

    @ApiOperation("Export budget data to csv file")
    @GetMapping("$ENDPOINT_API_PROJECT_EXPORT/budget")
    fun exportBudget(
        @PathVariable projectId: Long,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam inputLanguage: SystemLanguage,
        @RequestParam(required = false) version: String? = null
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Export application form data to pdf file")
    @GetMapping("$ENDPOINT_API_PROJECT_EXPORT/application")
    fun exportApplicationForm(
        @PathVariable projectId: Long,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam inputLanguage: SystemLanguage,
        @RequestParam(required = false) version: String? = null
    ): ResponseEntity<ByteArrayResource>
}
