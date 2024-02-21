package io.cloudflight.jems.server.payments.repository.account.finance

import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.QPaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.entity.account.QPaymentAccountCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.entity.account.QPaymentAccountEntity
import io.cloudflight.jems.server.payments.entity.account.QPaymentAccountPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewContribution
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledPriority
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledScenario
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.CorrectionAmountWithdrawn
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.service.account.finance.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.programme.entity.QProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.QProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionMeasureEntity
import io.cloudflight.jems.server.project.entity.lumpsum.QProjectLumpSumEntity
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class PaymentAccountFinancePersistenceProvider(
    private val jpaQueryFactory: JPAQueryFactory,
) : PaymentAccountFinancePersistence {

    companion object {
        private val accountCorrection = QAuditControlCorrectionEntity.auditControlCorrectionEntity
        private val accountCorrectionFinance = QAuditControlCorrectionFinanceEntity.auditControlCorrectionFinanceEntity
        private val correctionExtensionToEc = QPaymentToEcCorrectionExtensionEntity.paymentToEcCorrectionExtensionEntity
        private val correctionExtensionToAcc =
            QPaymentAccountCorrectionExtensionEntity.paymentAccountCorrectionExtensionEntity
        private val payment = QPaymentEntity.paymentEntity
        private val projectLumpSum = QProjectLumpSumEntity.projectLumpSumEntity
        private val paymentExtension = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
        private val project = QProjectEntity.projectEntity

        private val priorityPolicy = QProgrammeSpecificObjectiveEntity.programmeSpecificObjectiveEntity
        private val correctionProgrammeMeasure =
            QAuditControlCorrectionMeasureEntity.auditControlCorrectionMeasureEntity
        private val correctionFinance =
            QAuditControlCorrectionFinanceEntity.auditControlCorrectionFinanceEntity

        /** Summary */
        private val programmePriority = QProgrammePriorityEntity.programmePriorityEntity
        private val paymentToEc = QPaymentApplicationToEcEntity.paymentApplicationToEcEntity
        private val paymentToEcPriorityAxisOverview = QPaymentToEcPriorityAxisOverviewEntity.paymentToEcPriorityAxisOverviewEntity
        private val paymentAccount = QPaymentAccountEntity.paymentAccountEntity
        private val paymentAccountPriorityAxisOverview = QPaymentAccountPriorityAxisOverviewEntity.paymentAccountPriorityAxisOverviewEntity
    }

    @Transactional(readOnly = true)
    override fun getCorrectionsOnlyDeductionsAndNonClericalMistake(
        fundId: Long,
        accountingYearId: Long
    ): Iterable<CorrectionAmountWithdrawn> {
        return jpaQueryFactory
            .select(
                accountCorrection.id,
                accountCorrection.auditControl.controllingBody,
                project,
                accountCorrectionFinance.fundAmount
                    .add(accountCorrectionFinance.publicContribution)
                    .add(accountCorrectionFinance.autoPublicContribution),
                accountCorrectionFinance.fundAmount
                    .add(accountCorrectionFinance.publicContribution)
                    .add(accountCorrectionFinance.autoPublicContribution)
                    .add(accountCorrectionFinance.privateContribution),
                paymentExtension.paymentApplicationToEc.id, // whenFound
                paymentExtension.paymentApplicationToEc.accountingYear, // whenFound
                correctionExtensionToEc.paymentApplicationToEc.id, // whenIncluded
                correctionExtensionToEc.paymentApplicationToEc.accountingYear // whenIncluded
            ).from(accountCorrection)
            .innerJoin(correctionExtensionToEc)
                .on(correctionExtensionToEc.correction.eq(accountCorrection))
            .leftJoin(project)
                .on(project.eq(accountCorrection.auditControl.project))
            .leftJoin(accountCorrectionFinance)
                .on(accountCorrectionFinance.correction.eq(accountCorrection))
            .leftJoin(projectLumpSum)
                .on(projectLumpSum.eq(accountCorrection.lumpSum))
            .leftJoin(payment)
                .on(
                    payment.fund.eq(accountCorrection.programmeFund)
                        .and(payment.projectReport.eq(accountCorrection.partnerReport.projectReport)
                            .or(payment.projectLumpSum.eq(projectLumpSum))
                    )
                )
            .leftJoin(paymentExtension)
                .on(paymentExtension.payment.eq(payment))
            .where(
                correctionExtensionToEc.paymentApplicationToEc.accountingYear.id.eq(accountingYearId)
                    .and(accountCorrection.programmeFund.id.eq(fundId))
                    .and(accountCorrectionFinance.deduction.isTrue()) // only negatives
                    .and(accountCorrectionFinance.clericalTechnicalMistake.isFalse()) // non clerical mistake
                    .and(paymentExtension.paymentApplicationToEc.isNotNull())
            ) // to group by year we need to have yearWhenFound
            .fetch()
            .map { it: Tuple ->
                CorrectionAmountWithdrawn(
                    id = it.get(0, Long::class.java)!!,
                    controllingBody = it.get(1, ControllingBody::class.java)!!,
                    priorityAxis = it.get(2, ProjectEntity::class.java)!!.priorityPolicy!!.programmePriority!!.code,
                    public = it.get(3, BigDecimal::class.java)!!,
                    total = it.get(4, BigDecimal::class.java)!!,
                    ecPaymentIdWhenFound = it.get(5, Long::class.java)!!,
                    yearWhenFound = it.get(6, AccountingYearEntity::class.java)!!.toModel(),
                    ecPaymentIdWhenIncluded = it.get(7, Long::class.java)!!,
                    yearWhenIncluded = it.get(8, AccountingYearEntity::class.java)!!.toModel(),
                )
            }
    }

    @Transactional(readOnly = true)
    override fun getTotalsForFinishedEcPayments(ecPaymentIds: Set<Long>): Map<Long?, PaymentToEcAmountSummaryLine> {
        val totalEligibleExpr = paymentToEcPriorityAxisOverview.totalEligibleExpenditure.sum()
        val totalUnionExpr = paymentToEcPriorityAxisOverview.totalUnionContribution.sum()
        val totalPublicExpr = paymentToEcPriorityAxisOverview.totalPublicContribution.sum()

        return jpaQueryFactory
            .select(
                programmePriority.id,
                programmePriority.code,
                totalEligibleExpr,
                totalUnionExpr,
                totalPublicExpr,
            )
            .from(paymentToEcPriorityAxisOverview)
            .leftJoin(programmePriority)
                .on(programmePriority.id.eq(paymentToEcPriorityAxisOverview.priorityAxis.id))
            .where(paymentToEcPriorityAxisOverview.paymentApplicationToEc.id.`in`(ecPaymentIds))
            .groupBy(programmePriority.id)
            .fetch()
            .associate { it: Tuple ->
                it.get(programmePriority.id) to PaymentToEcAmountSummaryLine(
                    priorityAxis = it.get(programmePriority.code),
                    totalEligibleExpenditure = it.get(totalEligibleExpr)!!,
                    totalUnionContribution = it.get(totalUnionExpr)!!,
                    totalPublicContribution = it.get(totalPublicExpr)!!,
                )
            }
    }

    @Transactional(readOnly = true)
    override fun getOverviewContributionForPaymentAccounts(): Map<Long, PaymentAccountOverviewContribution> {
        val accountOverviewContribution = getOverviewContributionForFinishedPaymentAccount()
        val ecOverviewContribution = getOverviewContributionForFinishedEcPayments()

        return accountOverviewContribution.mergeWith(ecOverviewContribution)
    }

    private fun getOverviewContributionForFinishedPaymentAccount(): Map<Long, PaymentAccountOverviewContribution> {
        val totalEligibleAccountExpr = paymentAccountPriorityAxisOverview.totalEligibleExpenditure.sum().coalesce(BigDecimal.ZERO)
        val totalPublicAccountExpr = paymentAccountPriorityAxisOverview.totalPublicContribution.sum().coalesce(BigDecimal.ZERO)

        return jpaQueryFactory
            .select(
                paymentAccount.id,
                totalEligibleAccountExpr,
                totalPublicAccountExpr,
            )
            .from(paymentAccount)
            .leftJoin(paymentAccountPriorityAxisOverview)
                .on(paymentAccountPriorityAxisOverview.paymentAccount.id.eq(paymentAccount.id))
            .where(paymentAccount.status.eq(PaymentAccountStatus.FINISHED))
            .groupBy(paymentAccount.id)
            .fetch()
            .associate {
                it.get(paymentAccount.id)!! to PaymentAccountOverviewContribution(
                    totalEligibleExpenditure = it.get(totalEligibleAccountExpr)!!,
                    totalPublicContribution = it.get(totalPublicAccountExpr)!!
                )
            }
    }

    fun getOverviewContributionForFinishedEcPayments(): Map<Long, PaymentAccountOverviewContribution> {
        val totalEligibleExpr = paymentToEcPriorityAxisOverview.totalEligibleExpenditure.sum().coalesce(BigDecimal.ZERO)
        val totalUnionExpr = paymentToEcPriorityAxisOverview.totalUnionContribution.sum().coalesce(BigDecimal.ZERO)
        val totalPublicExpr = paymentToEcPriorityAxisOverview.totalPublicContribution.sum().coalesce(BigDecimal.ZERO)

        return jpaQueryFactory
            .select(
                paymentAccount.id,
                totalEligibleExpr,
                totalUnionExpr,
                totalPublicExpr
            )
            .from(paymentAccount)
            .leftJoin(paymentToEc)
                .on(paymentToEc.accountingYear.eq(paymentAccount.accountingYear)
                    .and(paymentToEc.programmeFund.eq(paymentAccount.programmeFund)))
            .leftJoin(paymentToEcPriorityAxisOverview)
                .on(paymentToEcPriorityAxisOverview.paymentApplicationToEc.eq(paymentToEc))
            .where(paymentAccount.status.eq(PaymentAccountStatus.FINISHED))
            .groupBy(paymentAccount.id)
            .fetch()
            .associate {
                it.get(paymentAccount.id)!! to PaymentAccountOverviewContribution(
                    totalEligibleExpenditure = it.get(totalEligibleExpr)!!.plus(it.get(totalUnionExpr)!!),
                    totalPublicContribution = it.get(totalPublicExpr)!!
                )
            }
    }


    @Transactional(readOnly = true)
    override fun getReconciliationOverview(
        paymentAccountId: Long,
        scenario: ReconciledScenario
    ): List<ReconciledPriority> =
        jpaQueryFactory
            .select(
                programmePriority.id,
                programmePriority.code,
                correctionExtensionToAcc.total().sum(),
                CaseBuilder().`when`(accountCorrection.auditControl.controllingBody.eq(ControllingBody.AA))
                    .then(correctionExtensionToAcc.total())
                    .otherwise(BigDecimal.ZERO).sum(),
                CaseBuilder().`when`(accountCorrection.auditControl.controllingBody.`in`(ControllingBody.ecOrEcaOrOlafInvestigations))
                    .then(correctionExtensionToAcc.total())
                    .otherwise(BigDecimal.ZERO).sum(),
            )
            .from(correctionExtensionToAcc)
            .leftJoin(accountCorrection)
                .on(accountCorrection.id.eq(correctionExtensionToAcc.correctionId))
            .leftJoin(correctionProgrammeMeasure)
                .on(correctionProgrammeMeasure.correction.eq(accountCorrection))
            .join(correctionFinance)
                .on(correctionFinance.correction.eq(accountCorrection))
            .leftJoin(project)
                .on(project.eq(accountCorrection.auditControl.project))
            .leftJoin(priorityPolicy)
                .on(priorityPolicy.programmeObjectivePolicy.eq(project.priorityPolicy.programmeObjectivePolicy))
            .leftJoin(programmePriority)
                .on(programmePriority.id.eq(priorityPolicy.programmePriority.id))
            .where(
                correctionExtensionToAcc.paymentAccount.id.eq(paymentAccountId).and(
                    when (scenario) {
                        ReconciledScenario.Scenario4 ->
                            correctionProgrammeMeasure.scenario.eq(ProjectCorrectionProgrammeMeasureScenario.SCENARIO_4)
                                .and(correctionFinance.clericalTechnicalMistake.isFalse)

                        ReconciledScenario.Scenario3 ->
                            correctionProgrammeMeasure.scenario.eq(ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3)
                                .and(correctionFinance.clericalTechnicalMistake.isFalse)

                        ReconciledScenario.ClericalMistakes ->
                            correctionFinance.clericalTechnicalMistake.isTrue()
                    }
                )
            )
            .groupBy(programmePriority.id)
            .fetch()
            .map { it: Tuple ->
                ReconciledPriority(
                    priorityId = it.get(0, Long::class.java)!!,
                    priorityCode = it.get(1, String::class.java)!!,
                    total = it.get(2, BigDecimal::class.java)!!,
                    ofWhichAa = it.get(3, BigDecimal::class.java)!!,
                    ofWhichEc = it.get(4, BigDecimal::class.java)!!,
                )
            }

    private fun QPaymentAccountCorrectionExtensionEntity.total() =
        fundAmount
            .add(publicContribution)
            .add(autoPublicContribution)
            .add(privateContribution)

}


