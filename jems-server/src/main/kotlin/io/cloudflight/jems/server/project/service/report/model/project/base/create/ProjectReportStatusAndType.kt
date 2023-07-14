package io.cloudflight.jems.server.project.service.report.model.project.base.create

import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus

data class ProjectReportStatusAndType(
    val id: Long,
    val status: ProjectReportStatus,
    val type: ContractingDeadlineType,
)
