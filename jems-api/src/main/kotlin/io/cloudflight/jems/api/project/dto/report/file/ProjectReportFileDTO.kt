package io.cloudflight.jems.api.project.dto.report.file

import java.time.ZonedDateTime

data class ProjectReportFileDTO(
    val id: Long,
    val name: String,
    val type: ProjectPartnerReportFileTypeDTO,
    val uploaded: ZonedDateTime,
    val author: UserSimpleDTO,
    val size: Long,
    val sizeString: String,
    val description: String,
)
