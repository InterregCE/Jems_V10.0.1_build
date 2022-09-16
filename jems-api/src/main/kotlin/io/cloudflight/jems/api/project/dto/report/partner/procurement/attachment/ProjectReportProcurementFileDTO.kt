package io.cloudflight.jems.api.project.dto.report.partner.procurement.attachment

import io.cloudflight.jems.api.project.dto.report.file.ProjectPartnerReportFileTypeDTO
import io.cloudflight.jems.api.project.dto.report.file.UserSimpleDTO
import java.time.ZonedDateTime

data class ProjectReportProcurementFileDTO(
    val id: Long,
    val reportId: Long,
    val createdInThisReport: Boolean,
    val name: String,
    val type: ProjectPartnerReportFileTypeDTO,
    val uploaded: ZonedDateTime,
    val author: UserSimpleDTO,
    val size: Long,
    val sizeString: String,
    val description: String,
)
