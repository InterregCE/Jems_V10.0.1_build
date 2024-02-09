package io.cloudflight.jems.server.payments.repository.account.finance.correction

import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.entity.account.QPaymentAccountCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewType
import io.cloudflight.jems.server.payments.repository.account.PaymentAccountRepository
import io.cloudflight.jems.server.payments.repository.regular.joinWithAnd
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.programme.entity.QProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.QProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.repository.priority.ProgrammePriorityRepository
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlEntity
import io.cloudflight.jems.server.project.entity.contracting.QProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class PaymentAccountCorrectionLinkingPersistenceProvider(
    private val paymentAccountRepository: PaymentAccountRepository,
    private val correctionExtensionRepository: PaymentAccountCorrectionExtensionRepository,
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val programmePriorityRepository: ProgrammePriorityRepository,
    private val priorityAxisOverviewRepository: PaymentAccountPriorityAxisOverviewRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : PaymentAccountCorrectionLinkingPersistence {

    @Transactional(readOnly = true)
    override fun getCorrectionExtension(correctionId: Long): PaymentAccountCorrectionExtension =
        correctionExtensionRepository.getById(correctionId).toModel()

    @Transactional
    override fun selectCorrectionToPaymentAccount(correctionIds: Set<Long>, paymentAccountId: Long) {
        val paymentAccount = paymentAccountRepository.getById(paymentAccountId)
        correctionExtensionRepository.findAllById(correctionIds).forEach {
            it.paymentAccount = paymentAccount
        }
    }

    @Transactional
    override fun deselectCorrectionFromPaymentAccountAndResetFields(correctionId: Long) {
        correctionExtensionRepository.findById(correctionId).get().also {
            it.paymentAccount = null
            it.correctedFundAmount = it.fundAmount
            it.correctedPublicContribution = it.publicContribution
            it.correctedAutoPublicContribution = it.autoPublicContribution
            it.correctedPrivateContribution = it.privateContribution
        }
    }

    @Transactional
    override fun createCorrectionExtension(
        financialDescription: ProjectCorrectionFinancialDescription,
    ) {
        val correctionEntity = auditControlCorrectionRepository.getById(financialDescription.correctionId)
        correctionExtensionRepository.save(
            financialDescription.toEntity(correctionEntity)
        )
    }

    @Transactional
    override fun updateCorrectionLinkedToPaymentAccountCorrectedAmounts(
        correctionId: Long,
        correctionLinkingUpdate: PaymentAccountCorrectionLinkingUpdate
    ): PaymentAccountCorrectionExtension =
        correctionExtensionRepository.getById(correctionId).apply {
            this.correctedAutoPublicContribution = correctionLinkingUpdate.correctedAutoPublicContribution
            this.correctedPublicContribution = correctionLinkingUpdate.correctedPublicContribution
            this.correctedPrivateContribution = correctionLinkingUpdate.correctedPrivateContribution
            this.correctedFundAmount = correctionLinkingUpdate.correctedFundAmount
            this.comment = correctionLinkingUpdate.comment
        }.toModel()


    @Transactional(readOnly = true)
    override fun getCorrectionIdsAvailableForPaymentAccounts(fundId: Long): Set<Long> {
        val specCorrection = QAuditControlCorrectionEntity.auditControlCorrectionEntity
        val specCorrectionExtension = QPaymentAccountCorrectionExtensionEntity.paymentAccountCorrectionExtensionEntity
        val whereExpressions = mutableListOf<BooleanExpression>(
            specCorrection.programmeFund.id.eq(fundId),
            specCorrection.status.eq(AuditControlStatus.Closed),
            specCorrectionExtension.paymentAccount.isNull,
        )

        return jpaQueryFactory
            .select(specCorrection.id)
            .from(specCorrection)
            .leftJoin(specCorrectionExtension)
            .on(specCorrection.id.eq(specCorrectionExtension.correctionId))
            .where(whereExpressions.joinWithAnd())
            .fetch()
            .toSet()
    }


    @Transactional(readOnly = true)
    override fun calculateOverviewForDraftPaymentAccount(paymentAccountId: Long): Map<Long?, PaymentAccountAmountSummaryLineTmp> {
        val correctionExtensionEntity = QPaymentAccountCorrectionExtensionEntity.paymentAccountCorrectionExtensionEntity
        val correctionEntity = QAuditControlCorrectionEntity.auditControlCorrectionEntity
        val priorityPolicy = QProgrammeSpecificObjectiveEntity.programmeSpecificObjectiveEntity
        val programmePriority = QProgrammePriorityEntity.programmePriorityEntity
        val specProjectAuditControl = QAuditControlEntity.auditControlEntity
        val specProjectEntity = QProjectEntity.projectEntity
        val specProjectContractingMonitoringEntity = QProjectContractingMonitoringEntity.projectContractingMonitoringEntity

        return jpaQueryFactory
            .select(
                programmePriority.id,
                programmePriority.code,
                correctionExtensionEntity.fundAmount.sum(),
                correctionExtensionEntity.correctedPublicContribution.sum(),
                correctionExtensionEntity.correctedAutoPublicContribution.sum(),
                correctionExtensionEntity.correctedPrivateContribution.sum(),
                correctionExtensionEntity.publicContribution.sum(),
                correctionExtensionEntity.autoPublicContribution.sum(),
                correctionExtensionEntity.privateContribution.sum(),
            )
            .from(correctionExtensionEntity)
            .leftJoin(correctionEntity)
            .on(correctionEntity.id.eq(correctionExtensionEntity.correctionId))
            .leftJoin(specProjectAuditControl)
            .on(specProjectAuditControl.id.eq(correctionEntity.auditControl.id))
            .leftJoin(specProjectEntity)
            .on(specProjectEntity.id.eq(specProjectAuditControl.project.id))
            .leftJoin(priorityPolicy)
            .on(priorityPolicy.programmeObjectivePolicy.eq(specProjectEntity.priorityPolicy.programmeObjectivePolicy))
            .leftJoin(programmePriority)
            .on(programmePriority.id.eq(priorityPolicy.programmePriority.id))
            .leftJoin(specProjectContractingMonitoringEntity)
            .on(specProjectContractingMonitoringEntity.projectId.eq(specProjectAuditControl.project.id))
            .where(correctionExtensionEntity.paymentAccount.id.eq(paymentAccountId))
            .groupBy(programmePriority.id)
            .fetch()
            .map { it: Tuple ->
                PaymentAccountAmountSummaryLineTmp(
                    priorityId = it.get(0, Long::class.java),
                    priorityAxis = it.get(1, String::class.java),
                    fundAmount = it.get(2, BigDecimal::class.java)!!,
                    partnerContribution = it.get(6, BigDecimal::class.java)!! +
                            it.get(7, BigDecimal::class.java)!! +
                            it.get(8, BigDecimal::class.java)!!,
                    ofWhichPublic = it.get(3, BigDecimal::class.java)!!,
                    ofWhichAutoPublic = it.get(4, BigDecimal::class.java)!!,
                )
            }.associateBy { it.priorityId }
    }

    @Transactional
    override fun saveTotalsWhenFinishingPaymentAccount(paymentAccountId: Long, totals: Map<Long?, PaymentAccountAmountSummaryLine>) {
        val priorityAxisIds = totals.keys.mapNotNull { it }

        val priorityById = programmePriorityRepository.findAllById(priorityAxisIds).associateBy { it.id }
        val paymentAccount = paymentAccountRepository.getById(paymentAccountId)

        priorityAxisOverviewRepository.deleteAllByPaymentAccountId(paymentAccountId)
        priorityAxisOverviewRepository.flush()
        priorityAxisOverviewRepository.saveAll(
            totals.map { (priorityId, summaryLine) ->
                PaymentAccountPriorityAxisOverviewEntity(
                    paymentAccount = paymentAccount,
                    priorityAxis = priorityById[priorityId],
                    type = PaymentAccountOverviewType.Correction,
                    totalEligibleExpenditure = summaryLine.totalEligibleExpenditure,
                    totalPublicContribution = summaryLine.totalPublicContribution,
                )
            }
        )
    }

    @Transactional(readOnly = true)
    override fun getTotalsForFinishedPaymentAccount(paymentAccountId: Long): Map<Long?, PaymentAccountAmountSummaryLine> =
        priorityAxisOverviewRepository.getAllByPaymentAccountIdAndType(paymentAccountId, PaymentAccountOverviewType.Correction).toModel()

}
