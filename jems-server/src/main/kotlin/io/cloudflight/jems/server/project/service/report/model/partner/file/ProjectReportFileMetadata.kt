package io.cloudflight.jems.server.project.service.report.model.partner.file

import java.time.ZonedDateTime

data class ProjectReportFileMetadata(
    val id: Long,
    val name: String,
    val uploaded: ZonedDateTime,
)
