package io.cloudflight.jems.server.project.controller.report.project.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.report.project.ProjectReportExportApi
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.project.service.report.project.export.ProjectReportExportInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class ProjectReportExportController(
    private val projectReportExportInteractor: ProjectReportExportInteractor
): ProjectReportExportApi {

    override fun exportReport(
        projectId: Long,
        reportId: Long,
        pluginKey: String,
        exportLanguage: SystemLanguage,
        inputLanguage: SystemLanguage,
        localDateTime: LocalDateTime
    ): ResponseEntity<ByteArrayResource> = projectReportExportInteractor.exportReport(
        projectId,
        reportId,
        pluginKey,
        exportLanguage,
        inputLanguage,
        localDateTime
    ).toResponseEntity()
}
