package io.cloudflight.jems.server.project.service.report.model.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata

data class ProjectPartnerReportWorkPackageOutput(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,

    val contribution: Boolean?,
    val evidence: Boolean?,
    val attachment: ProjectReportFileMetadata?,
)
