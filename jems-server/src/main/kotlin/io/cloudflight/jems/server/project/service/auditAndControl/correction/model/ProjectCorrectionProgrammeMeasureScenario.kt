package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

enum class ProjectCorrectionProgrammeMeasureScenario {
    NA,
    SCENARIO_1,
    SCENARIO_2,
    SCENARIO_3,
    SCENARIO_4,
    SCENARIO_5;

    fun allowsLinkingToEcPayment() = this in setOf(NA, SCENARIO_2, SCENARIO_5)
    fun allowsLinkingToPaymentAccount() = this in setOf(SCENARIO_3, SCENARIO_4)
}
