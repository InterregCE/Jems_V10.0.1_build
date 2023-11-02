package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

data class ProjectCorrectionProgrammeMeasureUpdate(
    val scenario: ProjectCorrectionProgrammeMeasureScenario,
    val comment: String?,
)
