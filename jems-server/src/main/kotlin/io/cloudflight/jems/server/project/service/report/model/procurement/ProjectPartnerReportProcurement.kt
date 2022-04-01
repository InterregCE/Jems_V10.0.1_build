package io.cloudflight.jems.server.project.service.report.model.procurement

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import java.math.BigDecimal

data class ProjectPartnerReportProcurement(
    val id: Long,
    val reportId: Long,
    val reportNumber: Int,
    var createdInThisReport: Boolean = false,
    val contractId: String,
    val contractType: Set<InputTranslation>,
    val contractAmount: BigDecimal,
    val supplierName: String,
    val comment: Set<InputTranslation>,
    val attachment: ProjectReportFileMetadata?,
)
