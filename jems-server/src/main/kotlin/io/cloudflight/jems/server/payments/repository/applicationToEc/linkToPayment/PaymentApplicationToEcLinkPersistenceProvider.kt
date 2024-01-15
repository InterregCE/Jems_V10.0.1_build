package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToPayment

import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.payments.entity.*
import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.payments.model.ec.overview.EcPaymentSummaryLine
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentApplicationsToEcRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcExtensionRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcPriorityAxisCumulativeOverviewRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcPriorityAxisOverviewRepository
import io.cloudflight.jems.server.payments.repository.regular.notFlagged
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.programme.entity.QProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.QProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.repository.priority.ProgrammePriorityRepository
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlEntity
import io.cloudflight.jems.server.project.entity.contracting.QProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class PaymentApplicationToEcLinkPersistenceProvider(
    private val ecPaymentRepository: PaymentApplicationsToEcRepository,
    private val ecPaymentExtensionRepository: PaymentToEcExtensionRepository,
    private val ecPaymentPriorityAxisOverviewRepository: PaymentToEcPriorityAxisOverviewRepository,
    private val ecPaymentPriorityAxisCumulativeOverviewRepository: PaymentToEcPriorityAxisCumulativeOverviewRepository,
    private val programmePriorityRepository: ProgrammePriorityRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : PaymentApplicationToEcLinkPersistence {

    companion object {
        private fun whereEcPaymentAndBasis(ecPaymentId: Long, scoBasis: PaymentSearchRequestScoBasis): BooleanExpression? {
            val paymentExtensionEntity = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
            val contractingMonitoringEntity = QProjectContractingMonitoringEntity.projectContractingMonitoringEntity

            return paymentExtensionEntity.paymentApplicationToEc.id.eq(ecPaymentId).and(
                when (scoBasis) {
                    PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95 -> contractingMonitoringEntity.notFlagged()
                    PaymentSearchRequestScoBasis.FallsUnderArticle94Or95 -> contractingMonitoringEntity.notFlagged().not()
                }
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getPaymentExtension(paymentId: Long): PaymentToEcExtension =
        ecPaymentExtensionRepository.getById(paymentId).toModel()

    @Transactional(readOnly = true)
    override fun getPaymentsLinkedToEcPayment(ecPaymentId: Long): Map<Long, PaymentInEcPaymentMetadata> {
        val paymentToEcExtension = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
        val payment = QPaymentEntity.paymentEntity
        val contractingMonitoring = QProjectContractingMonitoringEntity.projectContractingMonitoringEntity

        val results = jpaQueryFactory.select(
            payment.id,
            payment.type,
            contractingMonitoring.typologyProv94,
            contractingMonitoring.typologyProv95,
        )
            .from(paymentToEcExtension)
            .leftJoin(payment)
                .on(payment.id.eq(paymentToEcExtension.payment.id))
            .leftJoin(contractingMonitoring)
                .on(payment.project.id.eq(contractingMonitoring.projectId))
            .where(paymentToEcExtension.paymentApplicationToEc.id.eq(ecPaymentId))
            .fetch()
            .map { it: Tuple ->
                PaymentInEcPaymentMetadata(
                    paymentId = it.get(payment.id)!!,
                    type = it.get(payment.type)!!,
                    typologyProv94 = it.get(contractingMonitoring.typologyProv94),
                    typologyProv95 = it.get(contractingMonitoring.typologyProv95),
                )
            }

        return results.associateBy { it.paymentId }
    }

    @Transactional
    override fun selectPaymentToEcPayment(paymentIds: Set<Long>, ecPaymentId: Long) {
        val ecPayment = ecPaymentRepository.getById(ecPaymentId)
        ecPaymentExtensionRepository.findAllById(paymentIds).forEach {
            it.paymentApplicationToEc = ecPayment
        }
    }

    @Transactional
    override fun deselectPaymentFromEcPaymentAndResetFields(paymentIds: Set<Long>) {
        ecPaymentExtensionRepository.findAllById(paymentIds).forEach {
            it.paymentApplicationToEc = null
            it.correctedPublicContribution = it.publicContribution
            it.correctedAutoPublicContribution = it.autoPublicContribution
            it.correctedPrivateContribution = it.privateContribution
        }
    }


    @Transactional
    override fun updatePaymentToEcCorrectedAmounts(
        paymentId: Long,
        paymentToEcLinkingUpdate: PaymentToEcLinkingUpdate
    ) {
        ecPaymentExtensionRepository.getById(paymentId).apply {
            this.correctedAutoPublicContribution = paymentToEcLinkingUpdate.correctedAutoPublicContribution
            this.correctedPublicContribution = paymentToEcLinkingUpdate.correctedPublicContribution
            this.correctedPrivateContribution = paymentToEcLinkingUpdate.correctedPrivateContribution
        }
    }

    @Transactional
    override fun updatePaymentToEcFinalScoBasis(toUpdate: Map<Long, PaymentSearchRequestScoBasis>) {
        ecPaymentExtensionRepository.findAllById(toUpdate.keys).forEach {
            it.finalScoBasis = toUpdate[it.paymentId]!!
        }
    }

    @Transactional(readOnly = true)
    override fun calculateAndGetOverviewForDraftEcPayment(ecPaymentId: Long): Map<PaymentToEcOverviewType, Map<Long?, PaymentToEcAmountSummaryLineTmp>> =
        mapOf(
            PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95 to
                    calculateForPayments(ecPaymentId, PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95),
            PaymentToEcOverviewType.FallsUnderArticle94Or95 to emptyMap(),
            PaymentToEcOverviewType.Correction to calculateForCorrections(ecPaymentId),
        )

    private fun calculateForPayments(ecPaymentId: Long, scoBasis: PaymentSearchRequestScoBasis): Map<Long?, PaymentToEcAmountSummaryLineTmp> {
        val paymentToEcExtensionEntity = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
        val paymentEntity = QPaymentEntity.paymentEntity
        val priorityPolicy = QProgrammeSpecificObjectiveEntity.programmeSpecificObjectiveEntity
        val programmePriority = QProgrammePriorityEntity.programmePriorityEntity
        val projectContractingMonitoringEntity = QProjectContractingMonitoringEntity.projectContractingMonitoringEntity

         return jpaQueryFactory
            .select(
                programmePriority.id,
                programmePriority.code,
                paymentEntity.amountApprovedPerFund.sum(),
                paymentToEcExtensionEntity.partnerContribution.sum(),
                paymentToEcExtensionEntity.correctedPublicContribution.sum(),
                paymentToEcExtensionEntity.correctedAutoPublicContribution.sum(),
                paymentToEcExtensionEntity.correctedPrivateContribution.sum(),
            )
            .from(paymentToEcExtensionEntity)
            .leftJoin(paymentEntity)
                .on(paymentEntity.id.eq(paymentToEcExtensionEntity.payment.id))
            .leftJoin(priorityPolicy)
                .on(priorityPolicy.programmeObjectivePolicy.eq(paymentEntity.project.priorityPolicy.programmeObjectivePolicy))
            .leftJoin(programmePriority)
                .on(programmePriority.id.eq(priorityPolicy.programmePriority.id))
            .leftJoin(projectContractingMonitoringEntity)
                .on(projectContractingMonitoringEntity.projectId.eq(paymentEntity.project.id))
            .where(whereEcPaymentAndBasis(ecPaymentId, scoBasis))
            .groupBy(programmePriority.id)
            .fetch()
            .map { it: Tuple ->
                PaymentToEcAmountSummaryLineTmp(
                    priorityId = it.get(0, Long::class.java),
                    priorityAxis = it.get(1, String::class.java),
                    fundAmount = it.get(2, BigDecimal::class.java)!!,
                    partnerContribution = it.get(3, BigDecimal::class.java)!!,
                    ofWhichPublic = it.get(4, BigDecimal::class.java)!!,
                    ofWhichAutoPublic = it.get(5, BigDecimal::class.java)!!,
                    unionContribution = BigDecimal.ZERO,
                    correctedFundAmount = BigDecimal.ZERO
                )
            }.associateBy { it.priorityId }
    }

    private fun calculateForCorrections(ecPaymentId: Long):  Map<Long?, PaymentToEcAmountSummaryLineTmp> {
        val paymentToEcCorrectionExtensionEntity = QPaymentToEcCorrectionExtensionEntity.paymentToEcCorrectionExtensionEntity
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
                paymentToEcCorrectionExtensionEntity.fundAmount.sum(),
                paymentToEcCorrectionExtensionEntity.correctedPublicContribution.sum(),
                paymentToEcCorrectionExtensionEntity.correctedAutoPublicContribution.sum(),
                paymentToEcCorrectionExtensionEntity.correctedPrivateContribution.sum(),
                paymentToEcCorrectionExtensionEntity.publicContribution.sum(),
                paymentToEcCorrectionExtensionEntity.autoPublicContribution.sum(),
                paymentToEcCorrectionExtensionEntity.privateContribution.sum(),
                CaseBuilder().`when`(specProjectContractingMonitoringEntity.notFlagged())
                    .then(BigDecimal.ZERO).otherwise(paymentToEcCorrectionExtensionEntity.correctedUnionContribution).sum(),
                CaseBuilder().`when`(specProjectContractingMonitoringEntity.notFlagged())
                    .then(paymentToEcCorrectionExtensionEntity.fundAmount).otherwise(paymentToEcCorrectionExtensionEntity.correctedFundAmount).sum(),
            )
            .from(paymentToEcCorrectionExtensionEntity)
            .leftJoin(correctionEntity)
                .on(correctionEntity.id.eq(paymentToEcCorrectionExtensionEntity.correctionId))
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
            .where(paymentToEcCorrectionExtensionEntity.paymentApplicationToEc.id.eq(ecPaymentId))
            .groupBy(programmePriority.id)
            .fetch()
            .map { it: Tuple ->
                PaymentToEcAmountSummaryLineTmp(
                    priorityId = it.get(0, Long::class.java),
                    priorityAxis = it.get(1, String::class.java),
                    fundAmount = it.get(2, BigDecimal::class.java)!!,
                    partnerContribution = it.get(6, BigDecimal::class.java)!! +
                        it.get(7, BigDecimal::class.java)!! +
                        it.get(8, BigDecimal::class.java)!!,
                    ofWhichPublic = it.get(3, BigDecimal::class.java)!!,
                    ofWhichAutoPublic = it.get(4, BigDecimal::class.java)!!,
                    unionContribution = it.get(9, BigDecimal::class.java)!!,
                    correctedFundAmount = it.get(10, BigDecimal::class.java)!!
                )
            }.associateBy { it.priorityId }
    }
    @Transactional
    override fun saveTotalsWhenFinishingEcPayment(
        ecPaymentId: Long,
        totals: Map<PaymentToEcOverviewType, Map<Long?, PaymentToEcAmountSummaryLine>>,
    ) {
        val priorityAxisIds = totals.values.flatMapTo(HashSet()) { it.keys.mapNotNull { it } }

        val priorityById = programmePriorityRepository.findAllById(priorityAxisIds).associateBy { it.id }
        val ecPaymentEntity = ecPaymentRepository.getById(ecPaymentId)

        ecPaymentPriorityAxisOverviewRepository.deleteAllByPaymentApplicationToEcId(ecPaymentId)
        ecPaymentPriorityAxisOverviewRepository.flush()
        ecPaymentPriorityAxisOverviewRepository.saveAll(
            totals.flatMap { (type, perAxisList) ->
                perAxisList.map { (priorityId, summaryLine) ->
                    PaymentToEcPriorityAxisOverviewEntity(
                        paymentApplicationToEc = ecPaymentEntity,
                        type = type,
                        priorityAxis = priorityById[priorityId],
                        totalEligibleExpenditure = summaryLine.totalEligibleExpenditure,
                        totalUnionContribution = summaryLine.totalUnionContribution,
                        totalPublicContribution = summaryLine.totalPublicContribution,
                    )
                }
            }
        )
    }

    @Transactional(readOnly = true)
    override fun getTotalsForFinishedEcPayment(
        ecPaymentId: Long,
    ): Map<PaymentToEcOverviewType, Map<Long?, PaymentToEcAmountSummaryLine>> =
        PaymentToEcOverviewType.values().associateWith {
            ecPaymentPriorityAxisOverviewRepository
                .getAllByPaymentApplicationToEcIdAndType(ecPaymentId, it).toModel()
        }


    @Transactional(readOnly = true)
    override fun getCumulativeAmounts(finishedEcPaymentIds: Set<Long>): Map<Long?, EcPaymentSummaryLine> {
        val ecPaymentOverview = QPaymentToEcPriorityAxisOverviewEntity.paymentToEcPriorityAxisOverviewEntity

        return jpaQueryFactory.select(
            ecPaymentOverview.priorityAxis.id,
            ecPaymentOverview.totalEligibleExpenditure.sum(),
            ecPaymentOverview.totalUnionContribution.sum(),
            ecPaymentOverview.totalPublicContribution.sum(),
        )
            .from(ecPaymentOverview)
            .where(ecPaymentOverview.paymentApplicationToEc.id.`in`(finishedEcPaymentIds))
            .groupBy(ecPaymentOverview.priorityAxis)
            .fetch().associate {
                Pair(
                    it.get(0, Long::class.java),
                    EcPaymentSummaryLine(
                        totalEligibleExpenditure = it.get(1, BigDecimal::class.java)!!,
                        totalUnionContribution = it.get(2, BigDecimal::class.java)!!,
                        totalPublicContribution = it.get(3, BigDecimal::class.java)!!,
                    ),
                )
            }
    }

    @Transactional
    override fun saveCumulativeAmounts(ecPaymentId: Long, totals: Map<Long?, EcPaymentSummaryLine>) {
        val ecPaymentEntity = ecPaymentRepository.getById(ecPaymentId)
        val priorityById = programmePriorityRepository.findAllById(totals.keys.mapNotNullTo(HashSet()) { it }).associateBy { it.id }

        val toSave = totals.mapKeys { (id, _) -> priorityById[id] }

        ecPaymentPriorityAxisCumulativeOverviewRepository.saveAll(
            toSave.map { (priority, summaryLine) ->
                PaymentToEcPriorityAxisCumulativeOverviewEntity(
                    paymentApplicationToEc = ecPaymentEntity,
                    priorityAxis = priority,
                    totalEligibleExpenditure = summaryLine.totalEligibleExpenditure,
                    totalUnionContribution = summaryLine.totalUnionContribution,
                    totalPublicContribution = summaryLine.totalPublicContribution,
                )
            }
        )
    }

    @Transactional(readOnly = true)
    override fun getCumulativeTotalForEcPayment(ecPaymentId: Long): Map<Long?, PaymentToEcAmountSummaryLine> =
        ecPaymentPriorityAxisCumulativeOverviewRepository.getAllByPaymentApplicationToEcId(ecPaymentId).toOverviewModels()

    @Transactional(readOnly = true)
    override fun getPaymentToEcIdsProjectReportIncluded(projectReportId: Long): Set<Long> =
        ecPaymentExtensionRepository.findAllByPaymentApplicationToEcNotNullAndPaymentProjectReportId(projectReportId).map {
            it.paymentApplicationToEc!!.id
        }.toSet()

}
