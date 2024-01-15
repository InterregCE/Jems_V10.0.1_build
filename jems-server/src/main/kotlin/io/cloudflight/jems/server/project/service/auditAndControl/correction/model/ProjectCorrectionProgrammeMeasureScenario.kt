package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

enum class ProjectCorrectionProgrammeMeasureScenario {
    NA,
    SCENARIO_1,
    SCENARIO_2,
    SCENARIO_3,
    SCENARIO_4,
    SCENARIO_5;

    fun scenarioAllowsLinkingToEcPayment() = this == NA || this == SCENARIO_2 || this == SCENARIO_5
}
