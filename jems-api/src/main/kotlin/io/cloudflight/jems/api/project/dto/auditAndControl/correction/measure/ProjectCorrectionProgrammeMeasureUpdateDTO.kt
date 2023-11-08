package io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureScenarioDTO

data class ProjectCorrectionProgrammeMeasureUpdateDTO(
    val scenario: ProjectCorrectionProgrammeMeasureScenarioDTO,
    val comment: String?,
)
