package io.cloudflight.jems.api.project.report.partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.report.partner.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import org.springframework.core.io.ByteArrayResource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDateTime

@Api("Project Partner Report Export")
interface ProjectPartnerReportExportApi {

    companion object {
        const val ENDPOINT_API_PARTNER_REPORT_EXPORT = "$ENDPOINT_API_PROJECT_PARTNER_REPORT/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @GetMapping("$ENDPOINT_API_PARTNER_REPORT_EXPORT/export")
    fun exportReport(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestParam(required = true) pluginKey: String,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam inputLanguage: SystemLanguage,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) localDateTime: LocalDateTime,
    ): ResponseEntity<ByteArrayResource>
}