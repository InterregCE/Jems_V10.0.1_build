package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact

enum class AuditControlCorrectionImpactAction {
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
