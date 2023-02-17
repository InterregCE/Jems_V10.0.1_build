package io.cloudflight.jems.api.project.dto.report.partner.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO

data class ProjectPartnerReportWorkPackageActivityDeliverableDTO(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,

    val contribution: Boolean?,
    val evidence: Boolean?,
    val attachment: ProjectReportFileMetadataDTO?,
    val deactivated: Boolean
)
