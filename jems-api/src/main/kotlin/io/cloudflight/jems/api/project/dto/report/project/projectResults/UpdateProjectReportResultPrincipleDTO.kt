package io.cloudflight.jems.api.project.dto.report.project.projectResults

import io.cloudflight.jems.api.project.dto.InputTranslation

class UpdateProjectReportResultPrincipleDTO(
    val projectResults: List<UpdateProjectReportProjectResultDTO>,
    val sustainableDevelopmentDescription: Set<InputTranslation>,
    val equalOpportunitiesDescription: Set<InputTranslation>,
    val sexualEqualityDescription: Set<InputTranslation>
)
