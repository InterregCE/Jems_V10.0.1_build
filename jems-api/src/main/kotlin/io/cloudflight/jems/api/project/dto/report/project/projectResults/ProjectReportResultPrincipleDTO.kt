package io.cloudflight.jems.api.project.dto.report.project.projectResults

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.InputProjectHorizontalPrinciples

data class ProjectReportResultPrincipleDTO(
    val projectResults: List<ProjectReportProjectResultDTO>,
    val horizontalPrinciples: InputProjectHorizontalPrinciples,
    val sustainableDevelopmentDescription: Set<InputTranslation>,
    val equalOpportunitiesDescription: Set<InputTranslation>,
    val sexualEqualityDescription: Set<InputTranslation>
)
