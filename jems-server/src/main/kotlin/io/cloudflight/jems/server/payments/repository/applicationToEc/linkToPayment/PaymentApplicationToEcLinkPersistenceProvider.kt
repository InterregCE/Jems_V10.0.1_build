package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToPayment

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.payments.entity.PaymentEcCumulativeId
import io.cloudflight.jems.server.payments.entity.PaymentToEcPriorityAxisCumulativeOverviewEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.entity.QPaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentApplicationsToEcRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcExtensionRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcPriorityAxisCumulativeOverviewRepository
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcPriorityAxisOverviewRepository
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.programme.entity.QProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.QProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.repository.priority.ProgrammePriorityRepository
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
            paymentToEcExtension.finalScoBasis,
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
                    paymentId = it.get(0, Long::class.java)!!,
                    type = it.get(1, PaymentType::class.java)!!,
                    finalScoBasis = it.get(2, PaymentSearchRequestScoBasis::class.java),
                    typologyProv94 = it.get(3, ContractingMonitoringExtendedOption::class.java)!!,
                    typologyProv95 = it.get(4, ContractingMonitoringExtendedOption::class.java)!!,
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

    @Transactional(readOnly = true)
    override fun calculateAndGetOverview(ecPaymentId: Long): Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLineTmp>> {
        val paymentToEcExtensionEntity = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
        val paymentEntity = QPaymentEntity.paymentEntity
        val priorityPolicy = QProgrammeSpecificObjectiveEntity.programmeSpecificObjectiveEntity
        val programmePriority = QProgrammePriorityEntity.programmePriorityEntity

        val results = jpaQueryFactory
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
            .where(paymentToEcExtensionEntity.paymentApplicationToEc.id.eq(ecPaymentId))
            .groupBy(programmePriority.id)
            .fetch()
            .map { it: Tuple ->
                PaymentToEcAmountSummaryLineTmp(
                    priorityAxis = it.get(1, String::class.java),
                    fundAmount = it.get(2, BigDecimal::class.java)!!,
                    partnerContribution = it.get(3, BigDecimal::class.java)!!,
                    ofWhichPublic = it.get(4, BigDecimal::class.java)!!,
                    ofWhichAutoPublic = it.get(5, BigDecimal::class.java)!!,
                )
            }

        return mapOf(
            PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95 to results,
            PaymentSearchRequestScoBasis.FallsUnderArticle94Or95 to emptyList(),
        )
    }

    @Transactional
    override fun saveTotalsWhenFinishingEcPayment(
        ecPaymentId: Long,
        totals: Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLine>>,
    ) {
        val priorityAxisCodes = totals.values.flatMap { it.mapNotNull { it.priorityAxis } }

        val priorityByCode = programmePriorityRepository.getAllByCodeIn(priorityAxisCodes).associateBy { it.code }
        val ecPaymentEntity = ecPaymentRepository.getById(ecPaymentId)

        ecPaymentPriorityAxisOverviewRepository.deleteAllByPaymentApplicationToEcId(ecPaymentId)
        ecPaymentPriorityAxisOverviewRepository.saveAll(
            totals.flatMap { (type, perAxisList) ->
                perAxisList.map { perAxis ->
                    PaymentToEcPriorityAxisOverviewEntity(
                        paymentApplicationToEc = ecPaymentEntity,
                        type = type,
                        priorityAxis = perAxis.priorityAxis?.let { code -> priorityByCode[code]!! },
                        totalEligibleExpenditure = perAxis.totalEligibleExpenditure,
                        totalUnionContribution = perAxis.totalUnionContribution,
                        totalPublicContribution = perAxis.totalPublicContribution,
                    )
                }
            }
        )
    }

    @Transactional(readOnly = true)
    override fun getTotalsForFinishedEcPayment(
        ecPaymentId: Long,
    ): Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLine>> =
        PaymentSearchRequestScoBasis.values().associateWith {
            ecPaymentPriorityAxisOverviewRepository
                .getAllByPaymentApplicationToEcIdAndType(ecPaymentId, it).toModel()
        }


    @Transactional(readOnly = true)
    override fun getCumulativeAmountsOfFinishedEcPaymentsByFundAndAccountingYear(fundId: Long, accountingYearId: Long): List<PaymentToEcAmountSummaryLine> {
        val ecPaymentApplication = QPaymentApplicationToEcEntity.paymentApplicationToEcEntity
        val ecPaymentOverview = QPaymentToEcPriorityAxisOverviewEntity.paymentToEcPriorityAxisOverviewEntity

        return jpaQueryFactory.select(
            ecPaymentOverview.priorityAxis.code,
            ecPaymentOverview.totalEligibleExpenditure.sum(),
            ecPaymentOverview.totalUnionContribution.sum(),
            ecPaymentOverview.totalPublicContribution.sum(),
        ).from(ecPaymentOverview)
            .leftJoin(ecPaymentApplication)
                .on(ecPaymentApplication.id.eq(ecPaymentOverview.paymentApplicationToEc.id))
            .where(
                ecPaymentApplication.status.eq(PaymentEcStatus.Finished)
                    .and(ecPaymentApplication.programmeFund.id.eq(fundId))
                    .and(ecPaymentApplication.accountingYear.id.eq(accountingYearId))
            )
            .groupBy(ecPaymentOverview.priorityAxis)
            .fetch().map {
                PaymentToEcAmountSummaryLine(
                    priorityAxis = it.get(0, String::class.java),
                    totalEligibleExpenditure = it.get(1, BigDecimal::class.java)!!,
                    totalUnionContribution = it.get(2, BigDecimal::class.java)!!,
                    totalPublicContribution = it.get(3, BigDecimal::class.java)!!,
                )
            }

    }

    @Transactional
    override fun saveCumulativeAmounts(ecPaymentId: Long, totals: List<PaymentToEcAmountSummaryLine>) {
        val ecPaymentEntity = ecPaymentRepository.getById(ecPaymentId)
        val priorityAxisCodes = totals.mapNotNull { it.priorityAxis }
        val priorityByCode = programmePriorityRepository.getAllByCodeIn(priorityAxisCodes).associateBy { it.code }

       ecPaymentPriorityAxisCumulativeOverviewRepository.saveAll(
            totals.map {
                PaymentToEcPriorityAxisCumulativeOverviewEntity(
                    id = PaymentEcCumulativeId(
                        paymentApplicationToEc = ecPaymentEntity,
                        priorityAxis = priorityByCode[it.priorityAxis]!!
                    ),
                    totalEligibleExpenditure = it.totalEligibleExpenditure,
                    totalUnionContribution = it.totalUnionContribution,
                    totalPublicContribution = it.totalPublicContribution
                )
            }
        )
    }

    @Transactional(readOnly = true)
    override fun getCumulativeTotalForEcPayment(ecPaymentId: Long): List<PaymentToEcAmountSummaryLine> =
        ecPaymentPriorityAxisCumulativeOverviewRepository.getAllByIdPaymentApplicationToEcId(ecPaymentId).toOverviewModels()


    @Transactional
    override fun updatePaymentToEcFinalScoBasis(toUpdate: Map<Long, PaymentSearchRequestScoBasis>) {
        ecPaymentExtensionRepository.findAllById(toUpdate.keys).forEach {
            it.finalScoBasis = toUpdate[it.paymentId]!!
        }
    }

}
