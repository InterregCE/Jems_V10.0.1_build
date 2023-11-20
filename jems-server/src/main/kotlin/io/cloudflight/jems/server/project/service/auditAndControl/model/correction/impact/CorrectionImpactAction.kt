package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact

enum class CorrectionImpactAction {
    NA,
    RepaymentByProject,
    AdjustmentInNextPayment,
    BudgetReduction,
    RepaymentByNA;

    companion object {
        val MODIFICATION_IMPACTS = setOf(BudgetReduction)
        val PAYMENT_IMPACTS = setOf(RepaymentByProject, AdjustmentInNextPayment)
    }
}
