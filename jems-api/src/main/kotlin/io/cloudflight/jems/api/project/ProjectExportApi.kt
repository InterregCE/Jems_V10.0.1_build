package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDateTime

@Api("Project Export")
interface ProjectExportApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_EXPORT = "/api/project/{projectId}/export"
        private const val DEFAULT_APPLICATION_EXPORT_PLUGIN = "standard-application-form-export-plugin"
        private const val DEFAULT_BUDGET_EXPORT_PLUGIN = "standard-budget-export-plugin"
    }

    @ApiOperation("Export budget data to csv file")
    @GetMapping("$ENDPOINT_API_PROJECT_EXPORT/budget")
    fun exportBudget(
        @PathVariable projectId: Long,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam inputLanguage: SystemLanguage,
        @RequestParam(required = false) version: String? = null,
        @RequestParam(required = false) pluginKey: String? = DEFAULT_BUDGET_EXPORT_PLUGIN,
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Export application form data to pdf file")
    @GetMapping("$ENDPOINT_API_PROJECT_EXPORT/application")
    fun exportApplicationForm(
        @PathVariable projectId: Long,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam inputLanguage: SystemLanguage,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) localDateTime: LocalDateTime,
        @RequestParam(required = false) version: String? = null,
        @RequestParam(required = false) pluginKey: String? = DEFAULT_APPLICATION_EXPORT_PLUGIN,
    ): ResponseEntity<ByteArrayResource>
}
