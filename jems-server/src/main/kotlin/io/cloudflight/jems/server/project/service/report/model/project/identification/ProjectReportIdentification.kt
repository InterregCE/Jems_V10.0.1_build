package io.cloudflight.jems.server.project.service.report.model.project.identification

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportIdentification(
    val targetGroups: List<ProjectReportIdentificationTargetGroup>,
    val highlights: Set<InputTranslation>,
    val partnerProblems: Set<InputTranslation>,
    val deviations: Set<InputTranslation>,
    var spendingProfilePerPartner: ProjectReportSpendingProfile,
)

