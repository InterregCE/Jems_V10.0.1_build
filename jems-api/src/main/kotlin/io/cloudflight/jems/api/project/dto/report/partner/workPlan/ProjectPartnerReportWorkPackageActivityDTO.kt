package io.cloudflight.jems.api.project.dto.report.partner.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO

data class ProjectPartnerReportWorkPackageActivityDTO(
    val id: Long,
    val number: Int,
    val title: Set<InputTranslation>,
    val progress: Set<InputTranslation>,
    val deliverables: List<ProjectPartnerReportWorkPackageActivityDeliverableDTO>,
    val attachment: ProjectReportFileMetadataDTO?,
)
