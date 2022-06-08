package io.cloudflight.jems.server.project.service.report.model.file

import java.time.ZonedDateTime

data class ProjectReportFile(
    val id: Long,
    val name: String,
    val type: ProjectPartnerReportFileType,
    val uploaded: ZonedDateTime,
    val author: UserSimple,
    val size: Long,
)
