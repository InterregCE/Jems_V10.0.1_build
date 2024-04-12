package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure

data class AuditControlCorrectionMeasureUpdate(
    val scenario: ProjectCorrectionProgrammeMeasureScenario,
    val comment: String?,
)
