package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure

enum class ProjectCorrectionProgrammeMeasureScenario {
    NA,
    SCENARIO_1,
    SCENARIO_2,
    SCENARIO_3,
    SCENARIO_4,
    SCENARIO_5;

    companion object {
        val linkableToPaymentAccount = setOf(
            SCENARIO_3,
            SCENARIO_4,
        )
        val linkableToEcPayment = setOf(
            NA,
            SCENARIO_2,
            SCENARIO_5,
        )
    }

    fun allowsLinkingToEcPayment() = this in linkableToEcPayment
    fun allowsLinkingToPaymentAccount() = this in linkableToPaymentAccount
}
