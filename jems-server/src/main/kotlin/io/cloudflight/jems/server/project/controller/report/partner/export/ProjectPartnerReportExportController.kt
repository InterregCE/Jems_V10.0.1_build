package io.cloudflight.jems.server.project.controller.report.partner.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.report.partner.ProjectPartnerReportExportApi
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.project.service.report.partner.export.ProjectPartnerReportExportInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class ProjectPartnerReportExportController(
    private val projectPartnerReportExportInteractor: ProjectPartnerReportExportInteractor
) : ProjectPartnerReportExportApi {

    override fun exportReport(
        partnerId: Long,
        reportId: Long,
        pluginKey: String,
        exportLanguage: SystemLanguage,
        inputLanguage: SystemLanguage,
        localDateTime: LocalDateTime
    ): ResponseEntity<ByteArrayResource> =
        projectPartnerReportExportInteractor.exportReport(partnerId, reportId, pluginKey, exportLanguage, inputLanguage, localDateTime)
            .toResponseEntity()
}