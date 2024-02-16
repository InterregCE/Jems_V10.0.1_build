package io.cloudflight.jems.api.project.dto.report.project.identification

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportIdentificationDTO(
    val targetGroups: List<ProjectReportIdentificationTargetGroupDTO>,
    val highlights: Set<InputTranslation>,
    val partnerProblems: Set<InputTranslation>,
    val deviations: Set<InputTranslation>,
    val spendingProfilePerPartner: ProjectReportSpendingProfileDTO,
)
