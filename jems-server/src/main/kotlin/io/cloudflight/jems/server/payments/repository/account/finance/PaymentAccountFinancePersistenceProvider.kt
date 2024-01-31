package io.cloudflight.jems.server.payments.repository.account.finance

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.CorrectionAmountWithdrawn
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.account.PaymentAccountFinancePersistence
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.entity.lumpsum.QProjectLumpSumEntity
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
        private val correctionExtension = QPaymentToEcCorrectionExtensionEntity.paymentToEcCorrectionExtensionEntity
        private val payment = QPaymentEntity.paymentEntity
        private val projectLumpSum = QProjectLumpSumEntity.projectLumpSumEntity
        private val paymentExtension = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
        private val project = QProjectEntity.projectEntity
    }

    @Transactional(readOnly = true)
    override fun getCorrectionsOnlyDeductionsAndNonClericalMistakeAndOnlyFinished(
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
                correctionExtension.paymentApplicationToEc.id, // whenIncluded
                correctionExtension.paymentApplicationToEc.accountingYear // whenIncluded
            ).from(accountCorrection)
            .innerJoin(correctionExtension)
                .on(correctionExtension.correction.eq(accountCorrection))
            .leftJoin(project)
                .on(project.eq(accountCorrection.auditControl.project))
            .leftJoin(accountCorrectionFinance)
                .on(accountCorrectionFinance.correction.eq(accountCorrection))
            .leftJoin(projectLumpSum)
                .on(projectLumpSum.eq(accountCorrection.lumpSum))
            .leftJoin(payment)
                .on(payment.fund.eq(accountCorrection.programmeFund).and(
                    payment.projectReport.eq(accountCorrection.partnerReport.projectReport)
                        .or(payment.projectLumpSum.eq(projectLumpSum))
                ))
            .leftJoin(paymentExtension)
                .on(paymentExtension.payment.eq(payment))
            .where(correctionExtension.paymentApplicationToEc.accountingYear.id.eq(accountingYearId)
                .and(accountCorrection.programmeFund.id.eq(fundId))
                .and(accountCorrectionFinance.deduction.isTrue()) // only negatives
                .and(accountCorrectionFinance.clericalTechnicalMistake.isFalse()) // non clerical mistake
                .and(paymentExtension.paymentApplicationToEc.isNotNull())) // to group by year we need to have yearWhenFound
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

}
