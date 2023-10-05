package io.cloudflight.jems.server.payments.repository.applicationToEc

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcCumulativeAmountsEntity
import io.cloudflight.jems.server.payments.entity.QPaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummaryUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.priority.ProgrammePriorityRepository
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
    override fun createPaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail {
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
    override fun updatePaymentApplicationToEc(paymentApplicationsToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail {
        val programmeFund = programmeFundRepository.getById(paymentApplicationsToEcUpdate.programmeFundId)
        val accountingYear = accountingYearRepository.getById(paymentApplicationsToEcUpdate.accountingYearId)
        val existingEcPayment = paymentApplicationsToEcRepository.getById(paymentApplicationsToEcUpdate.id!!)

        existingEcPayment.update(programmeFund, accountingYear, paymentApplicationsToEcUpdate)

        return existingEcPayment.toDetailModel()
    }

    @Transactional
    override fun updatePaymentToEcSummaryOtherSection(paymentToEcUpdate: PaymentApplicationToEcSummaryUpdate): PaymentApplicationToEcDetail {
        val existingEcPayment = paymentApplicationsToEcRepository.getById(paymentToEcUpdate.id!!)

        existingEcPayment.updateOther(paymentToEcUpdate)

        return existingEcPayment.toDetailModel()
    }

    private fun PaymentApplicationToEcEntity.update(
        programmeFundEntity: ProgrammeFundEntity,
        accountingYearEntity: AccountingYearEntity,
        newData: PaymentApplicationToEcSummaryUpdate
    ): PaymentApplicationToEcEntity {
        this.programmeFund = programmeFundEntity
        this.accountingYear = accountingYearEntity
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
    override fun finalizePaymentApplicationToEc(paymentId: Long): PaymentApplicationToEcDetail =
        paymentApplicationsToEcRepository.getById(paymentId).apply {
            this.status = PaymentEcStatus.Finished
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
    override fun getPaymentsLinkedToEcPayment(ecPaymentId: Long): Map<Long, PaymentType> =
        paymentToEcExtensionRepository.findAllByPaymentApplicationToEcId(ecPaymentId = ecPaymentId)
            .associate { Pair(it.paymentId, it.payment.type) }

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
    override fun getSelectedPaymentsToEcPayment(ecPaymentId: Long): Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLineTmp>> {
        val paymentToEcExtensionEntity = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
        val paymentEntity = QPaymentEntity.paymentEntity

        val results = jpaQueryFactory
            .select(
                paymentEntity.project.priorityPolicy.programmePriority.code,
                paymentEntity.amountApprovedPerFund.sum(),
                paymentToEcExtensionEntity.partnerContribution.sum(),
                paymentToEcExtensionEntity.correctedPublicContribution.sum(),
                paymentToEcExtensionEntity.correctedAutoPublicContribution.sum(),
                paymentToEcExtensionEntity.correctedPrivateContribution.sum(),
            )
            .from(paymentToEcExtensionEntity)
            .leftJoin(paymentEntity)
                .on(paymentEntity.id.eq(paymentToEcExtensionEntity.payment.id))
            .where(paymentToEcExtensionEntity.paymentApplicationToEc.id.eq(ecPaymentId))
            .groupBy(paymentEntity.project.priorityPolicy.programmePriority)
            .fetch()
            .map { it: Tuple ->
                PaymentToEcAmountSummaryLineTmp(
                    priorityAxis = it.get(0, String::class.java),
                    fundAmount = it.get(1, BigDecimal::class.java)!!,
                    partnerContribution = it.get(2, BigDecimal::class.java)!!,
                    ofWhichPublic = it.get(3, BigDecimal::class.java)!!,
                    ofWhichAutoPublic = it.get(4, BigDecimal::class.java)!!,
                )
            }

        return mapOf(
            PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95 to results,
        )
    }

    @Transactional
    override fun saveCumulativeAmountsByType(
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
    override fun getSavedCumulativeAmountsForPaymentsToEcByType(
        ecPaymentId: Long,
    ): Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLine>> =
        PaymentSearchRequestScoBasis.values().associateWith {
            this.paymentToEcCumulativeAmountsRepository
                .getAllByPaymentApplicationToEcIdAndType(ecPaymentId, it).toModel()
        }

}
