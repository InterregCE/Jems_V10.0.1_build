package io.cloudflight.jems.server.project.service.report.model.partner.procurement

import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import java.time.ZonedDateTime

data class ProjectReportProcurementFile(
    val id: Long,
    val reportId: Long,
    var createdInThisReport: Boolean = false,
    val name: String,
    val type: JemsFileType,
    val uploaded: ZonedDateTime,
    val author: UserSimple,
    val size: Long,
    val description: String
)
