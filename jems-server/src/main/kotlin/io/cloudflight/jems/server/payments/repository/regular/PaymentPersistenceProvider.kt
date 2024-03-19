package io.cloudflight.jems.server.payments.repository.regular

import com.querydsl.core.Tuple
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.PaymentAttachment
import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.entity.QAccountingYearEntity
import io.cloudflight.jems.server.payments.entity.QPaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentPartnerEntity
import io.cloudflight.jems.server.payments.entity.QPaymentPartnerInstallmentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PartnerPaymentSimple
import io.cloudflight.jems.server.payments.model.regular.PaymentConfirmedInfo
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentPerPartner
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.PaymentToProjectTmp
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.model.regular.contributionMeta.ContributionMeta
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentFtlsToCreate
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentRegularToCreate
import io.cloudflight.jems.server.payments.repository.applicationToEc.PaymentToEcExtensionRepository
import io.cloudflight.jems.server.payments.repository.toDetailModel
import io.cloudflight.jems.server.payments.repository.toEntity
import io.cloudflight.jems.server.payments.repository.toFTLSPaymentEntity
import io.cloudflight.jems.server.payments.repository.toFTLSPaymentModel
import io.cloudflight.jems.server.payments.repository.toListModel
import io.cloudflight.jems.server.payments.repository.toModelList
import io.cloudflight.jems.server.payments.repository.toRegularPaymentEntity
import io.cloudflight.jems.server.payments.repository.toRegularPaymentModel
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.programme.entity.QProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.QProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.QProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.contracting.QProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.entity.lumpsum.QProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.project.QProjectReportEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.repository.lumpsum.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableFtlsTmp
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.PaymentCumulativeAmounts
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.PaymentCumulativeData
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate


@Repository
class PaymentPersistenceProvider(
    private val paymentRepository: PaymentRepository,
    private val paymentPartnerRepository: PaymentPartnerRepository,
    private val paymentPartnerInstallmentRepository: PaymentPartnerInstallmentRepository,
    private val paymentContributionMetaRepository: PaymentContributionMetaRepository,
    private val projectRepository: ProjectRepository,
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val projectLumpSumRepository: ProjectLumpSumRepository,
    private val userRepository: UserRepository,
    private val fundRepository: ProgrammeFundRepository,
    private val projectFileMetadataRepository: JemsFileMetadataRepository,
    private val projectReportCoFinancingRepository: ProjectReportCoFinancingRepository,
    private val projectReportRepository: ProjectReportRepository,
    private val fileRepository: JemsProjectFileService,
    private val projectPartnerReportRepository: ProjectPartnerReportRepository,
    private val paymentToEcExtensionRepository: PaymentToEcExtensionRepository,
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : PaymentPersistence {

    companion object {
        val payment = QPaymentEntity.paymentEntity
        private val paymentPartner = QPaymentPartnerEntity.paymentPartnerEntity
        val paymentPartnerInstallment = QPaymentPartnerInstallmentEntity.paymentPartnerInstallmentEntity
        private val projectLumpSum = QProjectLumpSumEntity.projectLumpSumEntity
        private val projectReport = QProjectReportEntity.projectReportEntity
        private val projectContracting = QProjectContractingMonitoringEntity.projectContractingMonitoringEntity
        private val project = QProjectEntity.projectEntity
        private val programmeSpecificObjective = QProgrammeSpecificObjectiveEntity.programmeSpecificObjectiveEntity
        private val programmePriority = QProgrammePriorityEntity.programmePriorityEntity
        val paymentToEcExtension = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity

        fun totalEligible() =
            payment.amountApprovedPerFund.add(paymentToEcExtension.partnerContribution)
        fun remainingToBePaid() =
            payment.amountApprovedPerFund.subtract(amountPaid())

        fun amountPaid() =
            CaseBuilder().`when`(paymentPartnerInstallment.isPaymentConfirmed.isTrue)
                .then(paymentPartnerInstallment.amountPaid)
                .otherwise(BigDecimal.ZERO)
                .sum()

        fun amountAuthorized() =
            CaseBuilder().`when`(paymentPartnerInstallment.isSavePaymentInfo.isTrue)
                .then(paymentPartnerInstallment.amountPaid)
                .otherwise(BigDecimal.ZERO)
                .sum()
    }

    @Transactional(readOnly = true)
    override fun existsById(id: Long) =
        paymentRepository.existsById(id)

    @Transactional(readOnly = true)
    override fun getAllPaymentToProject(pageable: Pageable, filters: PaymentSearchRequest): Page<PaymentToProject> {
        return fetchPayments(pageable, filters).map {
            when (it.payment.type) {
                PaymentType.REGULAR -> it.toRegularPaymentModel()
                PaymentType.FTLS -> it.toFTLSPaymentModel()
            }
        }
    }

    @Transactional(readOnly = true)
    override fun getAllPaymentToEcPayment(pageable: Pageable, filters: PaymentSearchRequest): Page<PaymentToEcPayment> {
        return fetchPayments(pageable, filters).map {
            PaymentToEcPayment(
                payment = when (it.payment.type) {
                    PaymentType.REGULAR -> it.toRegularPaymentModel()
                    PaymentType.FTLS -> it.toFTLSPaymentModel()
                },
                paymentToEcId = it.paymentToEcExtension.paymentToEcId,
                priorityAxis = it.code ?: "N/A",

                correctedTotalEligibleWithoutSco = it.paymentToEcExtension.correctedTotalEligibleWithoutSco,
                correctedFundAmountUnionContribution = it.paymentToEcExtension.correctedFundAmountUnionContribution,
                correctedFundAmountPublicContribution = it.paymentToEcExtension.correctedFundAmountPublicContribution,

                partnerContribution = it.paymentToEcExtension.partnerContribution,
                publicContribution = it.paymentToEcExtension.publicContribution,
                correctedPublicContribution = it.paymentToEcExtension.correctedPublicContribution,
                autoPublicContribution = it.paymentToEcExtension.autoPublicContribution,
                correctedAutoPublicContribution = it.paymentToEcExtension.correctedAutoPublicContribution,
                privateContribution = it.paymentToEcExtension.privateContribution,
                correctedPrivateContribution = it.paymentToEcExtension.correctedPrivateContribution,
                comment = it.paymentToEcExtension.comment,
            )
        }
    }

    private fun fetchPayments(pageable: Pageable, filters: PaymentSearchRequest): Page<PaymentToProjectTmp> {

        val results = jpaQueryFactory
            .select(
                payment,
                amountPaid(),
                amountAuthorized(),
                paymentPartnerInstallment.paymentDate.max(),
                totalEligible(),
                remainingToBePaid(),
                programmePriority.code,
                paymentToEcExtension.paymentApplicationToEc.id,
                paymentToEcExtension.correctedTotalEligibleWithoutSco,
                paymentToEcExtension.correctedFundAmountUnionContribution,
                paymentToEcExtension.correctedFundAmountPublicContribution,
                paymentToEcExtension.partnerContribution,
                paymentToEcExtension.publicContribution,
                paymentToEcExtension.correctedPublicContribution,
                paymentToEcExtension.autoPublicContribution,
                paymentToEcExtension.correctedAutoPublicContribution,
                paymentToEcExtension.privateContribution,
                paymentToEcExtension.correctedPrivateContribution,
                paymentToEcExtension.comment,
            )
            .from(payment)
                .leftJoin(paymentPartner)
                    .on(paymentPartner.payment.eq(payment))
                .leftJoin(paymentPartnerInstallment)
                    .on(paymentPartnerInstallment.paymentPartner.eq(paymentPartner))
                .leftJoin(projectLumpSum) // we need this manual join for MA-Approval filter to work
                    .on(projectLumpSum.eq(payment.projectLumpSum))
                .leftJoin(projectReport) // we need this manual join for MA-Approval filter to work
                    .on(projectReport.eq(payment.projectReport))
                .leftJoin(projectContracting)
                    .on(projectContracting.projectId.eq(payment.project.id))
                .leftJoin(project)
                    .on(project.eq(payment.project))
                .leftJoin(programmeSpecificObjective)
                    .on(programmeSpecificObjective.programmeObjectivePolicy.eq(project.priorityPolicy.programmeObjectivePolicy))
                .leftJoin(programmePriority)
                    .on(programmePriority.eq(programmeSpecificObjective.programmePriority))
                .leftJoin(paymentToEcExtension)
                    .on(paymentToEcExtension.payment.eq(payment))
            .where(filters.transformToWhereClause(payment, projectLumpSum, projectReport, projectContracting, paymentToEcExtension))
            .groupBy(payment)
            .having(filters.transformToHavingClause(paymentPartnerInstallment))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(pageable.sort.toQueryDslOrderBy())
            .fetchResults()

        return results.toPageResult(pageable)
    }

    @Transactional(readOnly = true)
    override fun getConfirmedInfosForPayment(paymentId: Long): PaymentConfirmedInfo {
        var amountPaid = BigDecimal.ZERO
        var amountAuthorized = BigDecimal.ZERO
        var lastPaymentDate: LocalDate? = null
        val paymentInstallments = paymentPartnerInstallmentRepository.findAllByPaymentPartnerPaymentId(paymentId)

        paymentInstallments.forEach { installment ->
            if (installment.isPaymentConfirmed == true) {
                amountPaid = amountPaid.add(installment.amountPaid)
                lastPaymentDate = if (installment.paymentDate != null && (lastPaymentDate == null || installment.paymentDate!!.isAfter(lastPaymentDate)))
                    installment.paymentDate else lastPaymentDate
            }
            amountAuthorized = if (installment.isSavePaymentInfo == true) amountAuthorized.add(installment.amountPaid) else amountAuthorized
        }
        return PaymentConfirmedInfo(
            id = paymentId,
            amountPaidPerFund = amountPaid,
            amountAuthorizedPerFund = amountAuthorized,
            dateOfLastPayment = lastPaymentDate
        )
    }

    @Transactional
    override fun deleteFTLSByProjectIdAndOrderNrIn(projectId: Long, orderNr: Set<Int>) =
        paymentRepository.deleteAllByProjectIdAndProjectLumpSumIdOrderNrInAndType(projectId, orderNr, PaymentType.FTLS)

    @Transactional
    override fun getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(
        projectId: Long,
        orderNrsToBeAdded: Set<Int>,
    ): List<PaymentPerPartner> =
        paymentRepository
            .getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId, orderNrsToBeAdded)
            .toListModel()

    @Transactional
    override fun saveFTLSPayments(projectId: Long, paymentsToBeSaved: Map<PaymentGroupingId, PaymentFtlsToCreate>) {
        val projectEntity = projectRepository.getReferenceById(projectId)
        val paymentEntities = paymentRepository.saveAll(paymentsToBeSaved.map { (id, model) ->
            model.toFTLSPaymentEntity(
                projectEntity = projectEntity,
                lumpSum = projectLumpSumRepository.getByIdProjectIdAndIdOrderNr(projectId, id.orderNr),
                fundEntity = fundRepository.getReferenceById(id.programmeFundId),
            )
        }).associateBy { PaymentGroupingId(it.projectLumpSum!!.id.orderNr, it.fund.id) }

        paymentEntities.forEach { (paymentId, paymentEntity) ->
            val toCreate = paymentsToBeSaved[paymentId]!!
            paymentPartnerRepository.saveAll(
                toCreate.partnerPayments.map {
                    it.toEntity(
                        paymentEntity = paymentEntity,
                        partnerEntity = projectPartnerRepository.getReferenceById(it.partnerId),
                        partnerReportEntity = null
                    )
                }
            )
            paymentToEcExtensionRepository.save(toCreate.toEntity(paymentEntity))
        }
    }

    @Transactional
    override fun saveRegularPayments(projectReportId: Long, paymentsToBeSaved: Map<Long, PaymentRegularToCreate>) {
        val projectReportEntity = projectReportRepository.getReferenceById(projectReportId)
        val projectEntity = projectRepository.getReferenceById(projectReportEntity.projectId)
        val fundIdToFundEntity =
            projectReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(projectReportId)
                .mapNotNull { it.programmeFund }.associateBy { it.id }

        val paymentEntities = paymentRepository.saveAll(
            paymentsToBeSaved.map { (fundId, toCreate) ->
                val fund = fundIdToFundEntity.getOrElse(fundId) { throw PaymentFinancingSourceNotFoundException() }
                toCreate.toRegularPaymentEntity(projectEntity, projectReportEntity, fund)
            }
        ).associateBy { it.fund.id }

        paymentEntities.forEach { (fundId, entity) ->
            val toCreate = paymentsToBeSaved[fundId]!!
            paymentPartnerRepository.saveAll(
                toCreate.partnerPayments.map {
                    it.toEntity(
                        paymentEntity = entity,
                        partnerEntity = projectPartnerRepository.getReferenceById(it.partnerId),
                        partnerReportEntity = it.partnerReportId?.let { projectPartnerReportRepository.getReferenceById(it) },
                    )
                }
            )
            paymentToEcExtensionRepository.save(toCreate.toEntity(entity))
        }
    }

    @Transactional(readOnly = true)
    override fun getPaymentDetails(paymentId: Long): PaymentDetail =
        paymentRepository.getReferenceById(paymentId).toDetailModel(
            partnerPayments = getAllPartnerPayments(paymentId)
        )

    @Transactional(readOnly = true)
    override fun getProjectIdForPayment(paymentId: Long): Long =
        paymentRepository.getReferenceById(paymentId).project.id

    @Transactional(readOnly = true)
    override fun getAllPartnerPayments(
        paymentId: Long
    ): List<PartnerPayment> =
        paymentPartnerRepository.findAllByPaymentId(paymentId)
            .map {
                it.toDetailModel(
                    installments = findPaymentPartnerInstallments(it.id),
                    partnerReportId = it.partnerCertificate?.id,
                    partnerReportNumber = it.partnerCertificate?.number
                )
            }

    @Transactional(readOnly = true)
    override fun getAllPartnerPaymentsForPartner(partnerId: Long) =
        paymentPartnerRepository.findAllByProjectPartnerId(partnerId).map {
            PartnerPaymentSimple(fundId = it.payment.fund.id, it.amountApprovedPerPartner ?: BigDecimal.ZERO)
        }

    @Transactional(readOnly = true)
    override fun getPaymentPartnerId(paymentId: Long, partnerId: Long): Long =
        paymentPartnerRepository.getIdByPaymentIdAndPartnerId(paymentId, partnerId)

    @Transactional(readOnly = true)
    override fun getPaymentPartnersIdsByPaymentId(paymentId: Long): List<Long> =
        paymentPartnerRepository.findAllByPaymentId(paymentId).map { it.id }


    @Transactional(readOnly = true)
    override fun findPaymentPartnerInstallments(paymentPartnerId: Long): List<PaymentPartnerInstallment> =
        this.paymentPartnerInstallmentRepository.findAllByPaymentPartnerId(paymentPartnerId).toModelList()

    @Transactional(readOnly = true)
    override fun findByPartnerId(partnerId: Long) =
        paymentPartnerInstallmentRepository.findAllByPaymentPartnerProjectPartnerId(partnerId).toModelList()

    @Transactional
    override fun updatePaymentPartnerInstallments(
        paymentPartnerId: Long,
        toDeleteInstallmentIds: Set<Long>,
        paymentPartnerInstallments: List<PaymentPartnerInstallmentUpdate>
    ): List<PaymentPartnerInstallment> {
        paymentPartnerInstallmentRepository.deleteAllByIdInBatch(toDeleteInstallmentIds)

        return paymentPartnerInstallmentRepository.saveAll(
            paymentPartnerInstallments.map {
                it.toEntity(
                    paymentPartner = paymentPartnerRepository.getReferenceById(paymentPartnerId),
                    savePaymentInfoUser = getUserOrNull(it.savePaymentInfoUserId),
                    paymentConfirmedUser = getUserOrNull(it.paymentConfirmedUserId),
                    correction = it.correctionId?.let { correctionId -> auditControlCorrectionRepository.getReferenceById(correctionId) },
                )
            }
        ).toModelList()
    }

    @Transactional
    override fun deletePaymentAttachment(fileId: Long) {
        fileRepository.delete(
            projectFileMetadataRepository.findByTypeAndId(PaymentAttachment, fileId) ?: throw ResourceNotFoundException("file")
        )
    }

    @Transactional(readOnly = true)
    override fun getPaymentsByProjectId(projectId: Long): List<PaymentToProject> {
        return paymentRepository.findAllByProjectId(projectId).toListModel(
            getConfirm = { id -> getConfirmedInfosForPayment(id) },
        )
    }

    @Transactional
    override fun storePartnerContributionsWhenReadyForPayment(contributions: Collection<ContributionMeta>) {
        paymentContributionMetaRepository.saveAll(contributions.toEntities())
    }

    @Transactional
    override fun deleteContributionsWhenReadyForPaymentReverted(projectId: Long, orderNrs: Set<Int>) {
        paymentContributionMetaRepository.deleteAll(
            paymentContributionMetaRepository.findByProjectIdAndLumpSumOrderNrIn(projectId, orderNrs)
        )
    }

    @Transactional(readOnly = true)
    override fun getFtlsCumulativeForPartner(partnerId: Long): ReportExpenditureCoFinancingColumn {
        val contribution = paymentContributionMetaRepository.getContributionCumulative(partnerId)
        val funds = paymentPartnerRepository.getPaymentOfTypeCumulativeForPartner(PaymentType.FTLS, partnerId).toMap()
            .plus(Pair(null, contribution.partnerContribution))
        return ReportExpenditureCoFinancingColumn(
            funds = funds,
            partnerContribution = contribution.partnerContribution,
            publicContribution = contribution.publicContribution,
            automaticPublicContribution = contribution.automaticPublicContribution,
            privateContribution = contribution.privateContribution,
            sum = funds.values.sumOf { it },
        )
    }

    @Transactional(readOnly = true)
    override fun getFtlsCumulativeForProject(projectId: Long): PaymentCumulativeData {
        val contribution = paymentContributionMetaRepository.getContributionCumulativePerProject(projectId)
        val funds = paymentPartnerRepository.getPaymentOfTypeCumulativeForProject(PaymentType.FTLS, projectId).toMap()

        val cumulativeAmounts = PaymentCumulativeAmounts(
            funds = funds,
            partnerContribution = contribution.partnerContribution,
            publicContribution = contribution.publicContribution,
            automaticPublicContribution = contribution.automaticPublicContribution,
            privateContribution = contribution.privateContribution,
        )

        return PaymentCumulativeData(
            amounts = cumulativeAmounts,
            confirmedAndPaid = paymentPartnerInstallmentRepository.getConfirmedCumulativeForProject(projectId).toMap(),
        )
    }

    @Transactional(readOnly = true)
    override fun getPaymentIdsAvailableForEcPayments(fundId: Long): Set<Long> {
        val specPayment = QPaymentEntity.paymentEntity
        val specPaymentToEcExtension = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
        val specProjectContracting = QProjectContractingMonitoringEntity.projectContractingMonitoringEntity
        val whereExpressions = mutableListOf<BooleanExpression>(
            specPayment.fund.id.eq(fundId),
            specPaymentToEcExtension.paymentApplicationToEc.isNull(),
        )


        return jpaQueryFactory
            .select(specPayment.id)
            .from(specPayment)
            .leftJoin(specPaymentToEcExtension)
            .on(specPayment.id.eq(specPaymentToEcExtension.paymentId))
            .leftJoin(specProjectContracting)
            .on(specProjectContracting.projectId.eq(specPayment.project.id))
            .where(whereExpressions.joinWithAnd())
            .fetch()
            .toSet()
    }

    @Transactional(readOnly = true)
    override fun getPaymentsLinkedToEcPayment(ecPaymentId: Long): List<PaymentToEcExtension> {
        return paymentToEcExtensionRepository.findAllByPaymentApplicationToEcId(ecPaymentId).toModelList()
    }

    @Transactional(readOnly = true)
    override fun getPaymentIdsInstallmentsExistsByProjectReportId(projectReportId: Long): Set<Long> =
        paymentPartnerInstallmentRepository.findAllByPaymentPartnerPaymentProjectReportId(projectReportId).map {
            it.paymentPartner.payment.id
        }.toSet()

    @Transactional
    override fun deleteRegularPayments(projectReportId: Long) {
        val regularPayments = paymentRepository.findAllByProjectReportId(projectReportId)
        regularPayments.forEach { regularPayment ->
            val attachments = projectFileMetadataRepository.findAllByPath(PaymentAttachment.generatePath(regularPayment.id))
            fileRepository.deleteBatch(attachments)
            paymentRepository.delete(regularPayment)
        }
    }

    @Transactional(readOnly = true)
    override fun getAvailableFtlsPayments(partnerIds: Set<Long>): List<CorrectionAvailableFtlsTmp> {
        val specPayment = QPaymentEntity.paymentEntity
        val specPaymentPartner = QPaymentPartnerEntity.paymentPartnerEntity
        val specProjectLumpSum = QProjectLumpSumEntity.projectLumpSumEntity
        val specFund = QProgrammeFundEntity.programmeFundEntity
        val specPaymentToEcExtension = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
        val specPaymentToEc = QPaymentApplicationToEcEntity.paymentApplicationToEcEntity
        val specAccountingYear = QAccountingYearEntity.accountingYearEntity

        return jpaQueryFactory.select(
            specPaymentPartner.projectPartner.id,
            specProjectLumpSum.programmeLumpSum,
            specProjectLumpSum.id.orderNr,
            specFund,
            specPaymentToEc.id,
            specPaymentToEc.status,
            specAccountingYear,
        )
            .from(specPayment)
            .leftJoin(specPaymentPartner).on(specPaymentPartner.payment.id.eq(specPayment.id))
            .leftJoin(specProjectLumpSum).on(specProjectLumpSum.id.eq(specPayment.projectLumpSum.id))
            .leftJoin(specFund).on(specFund.id.eq(specPayment.fund.id))
            .leftJoin(specPaymentToEcExtension).on(specPaymentToEcExtension.payment.id.eq(specPayment.id))
            .leftJoin(specPaymentToEc).on(specPaymentToEc.id.eq(specPaymentToEcExtension.paymentApplicationToEc.id))
            .leftJoin(specAccountingYear).on(specAccountingYear.id.eq(specPaymentToEc.accountingYear.id))
            .where(
                ExpressionUtils.allOf(
                    specPaymentPartner.projectPartner.id.`in`(partnerIds),
                    specProjectLumpSum.isReadyForPayment.isTrue,
                    specProjectLumpSum.programmeLumpSum.isFastTrack.isTrue
                )
            )
            .fetch()
            .map { it.toTmpModel() }
    }

    private fun Tuple.toTmpModel(): CorrectionAvailableFtlsTmp {
        val programmeLumpSum = get(1, ProgrammeLumpSumEntity::class.java)!!
        val lumpSumName = programmeLumpSum.translatedValues.map { InputTranslation(it.translationId.language, it.name) }.toSet()

        return CorrectionAvailableFtlsTmp(
            partnerId = get(0, Long::class.java)!!,
            programmeLumpSumId = programmeLumpSum.id,
            orderNr = get(2, Int::class.java)!!,
            name = lumpSumName,
            availableFund = get(3, ProgrammeFundEntity::class.java)!!.toModel(),
            ecPaymentId = get(4, Long::class.java),
            ecPaymentStatus = get(5, PaymentEcStatus::class.java),
            ecPaymentAccountingYear = get(6, AccountingYearEntity::class.java)?.toModel(),
        )
    }

    private fun getUserOrNull(userId: Long?): UserEntity? =
        if (userId != null) {
            userRepository.getReferenceById(userId)
        } else null

}

