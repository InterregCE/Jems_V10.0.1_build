package io.cloudflight.jems.api.project.dto.auditAndControl.correction

data class ProjectCorrectionProgrammeMeasureUpdateDTO(
    val scenario: ProjectCorrectionProgrammeMeasureScenarioDTO,
    val comment: String?,
)
