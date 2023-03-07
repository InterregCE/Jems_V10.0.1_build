package io.cloudflight.jems.server.project.service.report.model.project.projectResults

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples

data class ProjectReportResultPrinciple(
    val projectResults: List<ProjectReportProjectResult>,
    val horizontalPrinciples: ProjectHorizontalPrinciples,
    val sustainableDevelopmentDescription: Set<InputTranslation>,
    val equalOpportunitiesDescription: Set<InputTranslation>,
    val sexualEqualityDescription: Set<InputTranslation>,
)
