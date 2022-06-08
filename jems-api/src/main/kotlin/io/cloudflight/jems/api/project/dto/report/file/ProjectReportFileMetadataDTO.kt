package io.cloudflight.jems.api.project.dto.report.file

import java.time.ZonedDateTime

data class ProjectReportFileMetadataDTO(
    val id: Long,
    val name: String,
    val uploaded: ZonedDateTime,
)
