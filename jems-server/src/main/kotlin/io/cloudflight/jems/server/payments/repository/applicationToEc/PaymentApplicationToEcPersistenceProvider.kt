package io.cloudflight.jems.server.payments.repository.applicationToEc

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcCumulativeAmountsEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.programme.entity.QProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.QProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.priority.ProgrammePriorityRepository
import io.cloudflight.jems.server.project.entity.contracting.QProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class PaymentApplicationToEcPersistenceProvider(
    private val paymentApplicationsToEcRepository: PaymentApplicationsToEcRepository,
    private val paymentToEcExtensionRepository: PaymentToEcExtensionRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val accountingYearRepository: AccountingYearRepository,
    private val fileRepository: JemsSystemFileService,
    private val reportFileRepository: JemsFileMetadataRepository,
    private val paymentToEcCumulativeAmountsRepository: PaymentToEcCumulativeAmountsRepository,
    private val programmePriorityRepository: ProgrammePriorityRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : PaymentApplicationToEcPersistence {

    @Transactional
    override fun createPaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcCreate): PaymentApplicationToEcDetail {
        val programmeFund = programmeFundRepository.getById(paymentApplicationsToEcUpdate.programmeFundId)
        val accountingYear = accountingYearRepository.getById(paymentApplicationsToEcUpdate.accountingYearId)

        return paymentApplicationsToEcRepository.save(
            PaymentApplicationToEcEntity(
                programmeFund = programmeFund,
                accountingYear = accountingYear,
                status = PaymentEcStatus.Draft,
                nationalReference = paymentApplicationsToEcUpdate.nationalReference,
                technicalAssistanceEur = paymentApplicationsToEcUpdate.technicalAssistanceEur,
                submissionToSfcDate = paymentApplicationsToEcUpdate.submissionToSfcDate,
                sfcNumber = paymentApplicationsToEcUpdate.sfcNumber,
                comment = paymentApplicationsToEcUpdate.comment
            )
        ).toDetailModel()
    }

    @Transactional
    override fun updatePaymentApplicationToEc(
        paymentApplicationId: Long,
        paymentApplicationsToEcUpdate: PaymentApplicationToEcSummaryUpdate
    ): PaymentApplicationToEcDetail {
        val existingEcPayment = paymentApplicationsToEcRepository.getById(paymentApplicationId)
        existingEcPayment.update(paymentApplicationsToEcUpdate)
        return existingEcPayment.toDetailModel()
    }

    @Transactional
    override fun updatePaymentToEcSummaryOtherSection(paymentToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail {
        val existingEcPayment = paymentApplicationsToEcRepository.getById(paymentToEcUpdate.id!!)

        existingEcPayment.updateOther(paymentToEcUpdate)

        return existingEcPayment.toDetailModel()
    }

    private fun PaymentApplicationToEcEntity.update(newData: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcEntity {
        this.nationalReference = newData.nationalReference
        this.technicalAssistanceEur = newData.technicalAssistanceEur
        this.submissionToSfcDate = newData.submissionToSfcDate
        this.sfcNumber = newData.sfcNumber
        this.comment = newData.comment
        return this
    }

    private fun PaymentApplicationToEcEntity.updateOther(newData: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcEntity {
        this.nationalReference = newData.nationalReference
        this.technicalAssistanceEur = newData.technicalAssistanceEur
        this.submissionToSfcDate = newData.submissionToSfcDate
        this.sfcNumber = newData.sfcNumber
        this.comment = newData.comment
        return this
    }

    @Transactional(readOnly = true)
    override fun getPaymentApplicationToEcDetail(id: Long): PaymentApplicationToEcDetail =
        paymentApplicationsToEcRepository.getById(id).toDetailModel()

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<PaymentApplicationToEc> =
        paymentApplicationsToEcRepository.findAll(pageable).toModel()

    @Transactional
    override fun updatePaymentApplicationToEcStatus(
        paymentId: Long,
        status: PaymentEcStatus
    ): PaymentApplicationToEcDetail =
        paymentApplicationsToEcRepository.getById(paymentId).apply {
            this.status = status
        }.toDetailModel()


    @Transactional
    override fun deleteById(id: Long) {
        paymentApplicationsToEcRepository.deleteById(id)
    }

    @Transactional
    override fun deletePaymentToEcAttachment(fileId: Long) {
        fileRepository.delete(
            reportFileRepository.findByTypeAndId(JemsFileType.PaymentToEcAttachment, fileId)
                ?: throw ResourceNotFoundException("file")
        )
    }

    @Transactional(readOnly = true)
    override fun getPaymentExtension(paymentId: Long): PaymentToEcExtension =
        paymentToEcExtensionRepository.getById(paymentId).toModel()

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
        val ecPayment = paymentApplicationsToEcRepository.getById(ecPaymentId)
        paymentToEcExtensionRepository.findAllById(paymentIds).forEach {
            it.paymentApplicationToEc = ecPayment
        }
    }

    @Transactional
    override fun deselectPaymentFromEcPaymentAndResetFields(paymentId: Long) {
        val paymentExtension = paymentToEcExtensionRepository.findById(paymentId).get()
        paymentExtension.paymentApplicationToEc = null
        paymentExtension.correctedPublicContribution = paymentExtension.publicContribution
        paymentExtension.correctedAutoPublicContribution = paymentExtension.autoPublicContribution
        paymentExtension.correctedPrivateContribution = paymentExtension.privateContribution
    }


    @Transactional
    override fun updatePaymentToEcCorrectedAmounts(
        paymentId: Long,
        paymentToEcLinkingUpdate: PaymentToEcLinkingUpdate
    ) {
        paymentToEcExtensionRepository.getById(paymentId).apply {
            this.correctedAutoPublicContribution = paymentToEcLinkingUpdate.correctedAutoPublicContribution
            this.correctedPublicContribution = paymentToEcLinkingUpdate.correctedPublicContribution
            this.correctedPrivateContribution = paymentToEcLinkingUpdate.correctedPrivateContribution
        }
    }

    @Transactional(readOnly = true)
    override fun existsDraftByFundAndAccountingYear(programmeFundId: Long, accountingYearId: Long): Boolean =
        paymentApplicationsToEcRepository.existsByProgrammeFundIdAndAccountingYearIdAndStatus(programmeFundId, accountingYearId, PaymentEcStatus.Draft)

    @Transactional(readOnly = true)
    override fun getAvailableAccountingYearsForFund(programmeFundId: Long): List<AccountingYear> =
        paymentApplicationsToEcRepository.getAvailableAccountingYearForFund(programmeFundId).map { it.toModel() }


    @Transactional(readOnly = true)
    override fun calculateAndGetTotals(ecPaymentId: Long): Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLineTmp>> {
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
        val ecPaymentEntity = paymentApplicationsToEcRepository.getById(ecPaymentId)

        this.paymentToEcCumulativeAmountsRepository.saveAll(
            totals.flatMap { (type, perAxisList) ->
                perAxisList.map { perAxis ->
                    PaymentToEcCumulativeAmountsEntity(
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
            this.paymentToEcCumulativeAmountsRepository
                .getAllByPaymentApplicationToEcIdAndType(ecPaymentId, it).toModel()
        }

    @Transactional
    override fun updatePaymentToEcFinalScoBasis(toUpdate: Map<Long, PaymentSearchRequestScoBasis>) {
        paymentToEcExtensionRepository.findAllById(toUpdate.keys).forEach {
            it.finalScoBasis = toUpdate[it.paymentId]!!
        }
    }

}
