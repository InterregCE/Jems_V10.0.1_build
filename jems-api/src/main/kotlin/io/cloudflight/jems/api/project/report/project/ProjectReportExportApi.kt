package io.cloudflight.jems.api.project.report.project

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.report.project.ProjectReportApi.Companion.ENDPOINT_API_PROJECT_REPORT
import io.swagger.annotations.Api
import org.springframework.core.io.ByteArrayResource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDateTime

@Api("Project Report Export")
interface ProjectReportExportApi {

    @GetMapping("$ENDPOINT_API_PROJECT_REPORT/export")
    fun exportReport(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestParam(required = true) pluginKey: String,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam inputLanguage: SystemLanguage,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) localDateTime: LocalDateTime,
    ): ResponseEntity<ByteArrayResource>

}
