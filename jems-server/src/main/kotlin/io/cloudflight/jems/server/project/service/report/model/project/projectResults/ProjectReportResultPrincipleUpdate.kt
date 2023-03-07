package io.cloudflight.jems.server.project.service.report.model.project.projectResults

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectReportResultPrincipleUpdate(
    val projectResults: Map<Int, ProjectReportResultUpdate>,
    val sustainableDevelopmentDescription: Set<InputTranslation>,
    val equalOpportunitiesDescription: Set<InputTranslation>,
    val sexualEqualityDescription: Set<InputTranslation>,
)
