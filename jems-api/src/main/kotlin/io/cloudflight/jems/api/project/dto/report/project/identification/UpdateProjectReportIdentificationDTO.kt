package io.cloudflight.jems.api.project.dto.report.project.identification

import io.cloudflight.jems.api.project.dto.InputTranslation

data class UpdateProjectReportIdentificationDTO(
    val targetGroups: List<Set<InputTranslation>> = emptyList(),
    val highlights: Set<InputTranslation>,
    val partnerProblems: Set<InputTranslation>,
    val deviations: Set<InputTranslation>,
)
