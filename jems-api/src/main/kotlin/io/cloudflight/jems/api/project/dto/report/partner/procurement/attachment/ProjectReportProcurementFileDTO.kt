package io.cloudflight.jems.api.project.dto.report.partner.procurement.attachment

import io.cloudflight.jems.api.common.dto.file.JemsFileTypeDTO
import io.cloudflight.jems.api.common.dto.file.UserSimpleDTO
import java.time.ZonedDateTime

data class ProjectReportProcurementFileDTO(
    val id: Long,
    val reportId: Long,
    val createdInThisReport: Boolean,
    val name: String,
    val type: JemsFileTypeDTO,
    val uploaded: ZonedDateTime,
    val author: UserSimpleDTO,
    val size: Long,
    val sizeString: String,
    val description: String
)
