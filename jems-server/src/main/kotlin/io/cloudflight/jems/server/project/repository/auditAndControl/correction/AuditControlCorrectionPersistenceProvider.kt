package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.payments.entity.QPaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.entity.account.QPaymentAccountCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinking
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionSearchRequest
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionSearchRequest
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionTmp
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection.toModel
import io.cloudflight.jems.server.payments.repository.regular.joinWithAnd
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionMeasureEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlEntity
import io.cloudflight.jems.server.project.entity.contracting.QProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.entity.lumpsum.QProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.partner.QProjectPartnerReportEntity
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.tmpModel.AuditControlCorrectionLineTmp
import io.cloudflight.jems.server.project.repository.lumpsum.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AvailableCorrectionsForPayment
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.CorrectionImpactAction
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


@Repository
class AuditControlCorrectionPersistenceProvider(
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val reportExpenditureRepository: ProjectPartnerReportExpenditureRepository,
    private val projectLumpSumRepository: ProjectLumpSumRepository,
    private val projectStatusHistoryRepository: ProjectStatusHistoryRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : AuditControlCorrectionPersistence {

    companion object {
        val accountCorrection = QAuditControlCorrectionEntity.auditControlCorrectionEntity
        val audit = QAuditControlEntity.auditControlEntity
        val project = QProjectEntity.projectEntity
        val correctionProgrammeMeasure = QAuditControlCorrectionMeasureEntity.auditControlCorrectionMeasureEntity
        private val projectContractingMonitoring = QProjectContractingMonitoringEntity.projectContractingMonitoringEntity
        private val ecPayment = QPaymentApplicationToEcEntity.paymentApplicationToEcEntity
        private val partnerReport = QProjectPartnerReportEntity.projectPartnerReportEntity

        // only for FTLS
        private val projectLumpSum = QProjectLumpSumEntity.projectLumpSumEntity
        private val ftlsPayment = QPaymentEntity.paymentEntity

        val correctionExtensionToEc = QPaymentToEcCorrectionExtensionEntity.paymentToEcCorrectionExtensionEntity
        val correctionExtensionToAccount = QPaymentAccountCorrectionExtensionEntity.paymentAccountCorrectionExtensionEntity
    }

    @Transactional(readOnly = true)
    override fun getProjectIdForCorrection(correctionId: Long): Long =
        auditControlCorrectionRepository.getReferenceById(correctionId).auditControl.project.id

    @Transactional(readOnly = true)
    override fun getAllCorrectionsByAuditControlId(
        auditControlId: Long,
        pageable: Pageable
    ): Page<AuditControlCorrectionLineTmp> {
        val correction = QAuditControlCorrectionEntity.auditControlCorrectionEntity
        val correctionFinance = QAuditControlCorrectionFinanceEntity.auditControlCorrectionFinanceEntity
        val correctionMeasure = QAuditControlCorrectionMeasureEntity.auditControlCorrectionMeasureEntity

        val results = jpaQueryFactory
            .select(
                correction,
                correctionFinance.fundAmount,
                correctionFinance.publicContribution,
                correctionFinance.autoPublicContribution,
                correctionFinance.privateContribution,
                correctionMeasure.scenario,
            )
            .from(correction)
            .leftJoin(correctionFinance)
            .on(correctionFinance.correction.eq(correction))
            .leftJoin(correctionMeasure)
            .on(correctionMeasure.correction.eq(correction))
            .where(correction.auditControl.id.eq(auditControlId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetchResults()

        return PageImpl(
            results.results.map {
                val correctionEntity = it.get(correction)!!
                AuditControlCorrectionLineTmp(
                    correction = correctionEntity.toSimpleModel(),
                    partnerId = correctionEntity.partnerReport?.partnerId,
                    lumpSumPartnerId = correctionEntity.lumpSumPartnerId,
                    partnerNumber = correctionEntity.partnerReport?.identification?.partnerNumber,
                    partnerAbbreviation = correctionEntity.partnerReport?.identification?.partnerAbbreviation,
                    partnerRole = correctionEntity.partnerReport?.identification?.partnerRole,
                    reportNr = correctionEntity.partnerReport?.number,
                    lumpSumOrderNr = correctionEntity.lumpSum?.id?.orderNr,
                    followUpAuditNr = correctionEntity.followUpOfCorrection?.auditControl?.number,
                    followUpCorrectionNr = correctionEntity.followUpOfCorrection?.orderNr,
                    fund = correctionEntity.programmeFund?.toModel(),
                    fundAmount = it.get(correctionFinance.fundAmount) ?: BigDecimal.ZERO,
                    publicContribution = it.get(correctionFinance.publicContribution) ?: BigDecimal.ZERO,
                    autoPublicContribution = it.get(correctionFinance.autoPublicContribution) ?: BigDecimal.ZERO,
                    privateContribution = it.get(correctionFinance.privateContribution) ?: BigDecimal.ZERO,
                    impactProjectLevel = correctionEntity.impact,
                    scenario = it.get(correctionMeasure.scenario)!!,
                )
            },
            pageable,
            results.total
        )

    }

    @Transactional(readOnly = true)
    override fun getPreviousClosedCorrections(
        correctionId: Long
    ): List<AuditControlCorrection> {
        val currentCorrection = auditControlCorrectionRepository.getReferenceById(correctionId)
        val auditControl = currentCorrection.auditControl

        return auditControlCorrectionRepository.getAllByAuditControlAndStatusAndOrderNrBefore(
            auditControl = auditControl,
            status = AuditControlStatus.Closed,
            orderNr = currentCorrection.orderNr,
        ).map { it.toSimpleModel() }
    }

    @Transactional(readOnly = true)
    override fun getByCorrectionId(correctionId: Long): AuditControlCorrectionDetail =
        auditControlCorrectionRepository.getReferenceById(correctionId).toModel()

    @Transactional(readOnly = true)
    override fun getLastUsedOrderNr(auditControlId: Long): Int? =
        auditControlCorrectionRepository.findFirstByAuditControlIdOrderByOrderNrDesc(auditControlId)?.orderNr

    @Transactional
    override fun deleteCorrectionById(id: Long) =
        auditControlCorrectionRepository.deleteById(id)

    @Transactional
    override fun closeCorrection(correctionId: Long): AuditControlCorrection {
        val entity = auditControlCorrectionRepository.findById(correctionId).get()
        entity.status = AuditControlStatus.Closed
        return entity.toSimpleModel()
    }

    @Transactional(readOnly = true)
    override fun getOngoingCorrectionsByAuditControlId(auditControlId: Long): List<AuditControlCorrection> =
        auditControlCorrectionRepository.getAllByAuditControlIdAndStatus(auditControlId, AuditControlStatus.Ongoing)
            .map { it.toSimpleModel() }

    @Transactional
    override fun updateCorrection(
        correctionId: Long,
        data: AuditControlCorrectionUpdate
    ): AuditControlCorrectionDetail {
        val entity = auditControlCorrectionRepository.findById(correctionId).get()

        if (entity.partnerReport?.id != data.partnerReportId)
            entity.partnerReport = data.partnerReportId?.let { partnerReportRepository.getReferenceById(it) }

        if (entity.lumpSum?.id?.orderNr != data.lumpSumOrderNr) {
            entity.lumpSum = data.lumpSumOrderNr?.let { projectLumpSumRepository.getByIdProjectIdAndIdOrderNr(entity.auditControl.project.id, it) }
            entity.lumpSumPartnerId = data.partnerId
        }

        if (entity.programmeFund?.id != data.programmeFundId)
            entity.programmeFund = programmeFundRepository.getReferenceById(data.programmeFundId)

        if (entity.followUpOfCorrection?.id != data.followUpOfCorrectionId)
            entity.followUpOfCorrection = data.followUpOfCorrectionId?.let { auditControlCorrectionRepository.getReferenceById(it) }

        entity.followUpOfCorrectionType = data.correctionFollowUpType
        entity.repaymentDate = data.repaymentFrom
        entity.lateRepayment = data.lateRepaymentTo

        entity.procurementId = data.procurementId
        entity.costCategory = data.costCategory
        entity.expenditure =
            if (data.expenditureId != null) reportExpenditureRepository.getReferenceById(data.expenditureId) else null

        return entity.toModel()
    }

    @Transactional(readOnly = true)
    override fun getCorrectionAvailableCostItems(
        partnerReportId: Long,
        pageable: Pageable
    ): Page<CorrectionCostItem> {
        return reportExpenditureRepository.findAllByPartnerReportIdOrderById(
            reportId = partnerReportId,
            pageable = pageable
        ).toPagedModel()
    }

    @Transactional
    override fun updateModificationByCorrectionIds(projectId: Long, correctionIds: Set<Long>, statuses: List<ApplicationStatus>) {
        val latestStatus = projectStatusHistoryRepository.findFirstByProjectIdAndStatusInOrderByUpdatedDesc(
            projectId = projectId,
            statuses = statuses
        )
        auditControlCorrectionRepository.findAllById(correctionIds).onEach { it.projectModificationId = latestStatus.id }
    }

    @Transactional(readOnly = true)
    override fun getAvailableCorrectionsForPayments(projectId: Long): List<AvailableCorrectionsForPayment> =
        getSelectableCorrections(projectId, CorrectionImpactAction.PAYMENT_IMPACTS)
            .groupBy { it.partnerId() }
            .filterKeys { it != null }
            .map { (partnerId, corrections) -> AvailableCorrectionsForPayment(
                partnerId = partnerId!!,
                corrections = corrections.toSimpleModel(),
            ) }

    @Transactional(readOnly = true)
    override fun getAvailableCorrectionsForModification(projectId: Long): List<AuditControlCorrection> =
        getSelectableCorrections(projectId, CorrectionImpactAction.MODIFICATION_IMPACTS)
            .map { it.toSimpleModel() }

    @Transactional(readOnly = true)
    override fun getCorrectionsForModificationDecisions(projectId: Long): Map<Long, List<AuditControlCorrection>> {
        val correctionSpec = QAuditControlCorrectionEntity.auditControlCorrectionEntity
        val correctionPredicate = correctionSpec.auditControl.project.id.eq(projectId)
            .and(correctionSpec.impact.`in`(CorrectionImpactAction.MODIFICATION_IMPACTS))
            .and(correctionSpec.projectModificationId.isNotNull)

        return jpaQueryFactory
            .select(correctionSpec)
            .from(correctionSpec)
            .where(correctionPredicate)
            .fetch()
            .groupBy(
                keySelector = { it.projectModificationId!! },
                valueTransform = { it.toSimpleModel() },
            )
    }

    @Transactional(readOnly = true)
    override fun getCorrectionsLinkedToEcPayment(
        pageable: Pageable,
        filter: PaymentToEcCorrectionSearchRequest,
    ): Page<PaymentToEcCorrectionLinking> =
        fetchCorrectionsForPaymentToEc(pageable, filter).map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getCorrectionsLinkedToPaymentAccount(
        pageable: Pageable,
        filter: PaymentAccountCorrectionSearchRequest,
    ): Page<PaymentAccountCorrectionLinking> =
        fetchCorrectionsForPaymentAccount(pageable, filter).map { it.toModel() }

    @Transactional(readOnly = true)
    override fun existsByProcurementId(procurementId: Long): Boolean =
        auditControlCorrectionRepository.existsByProcurementId(procurementId)

    private fun getSelectableCorrections(projectId: Long, impacts: Set<CorrectionImpactAction>): List<AuditControlCorrectionEntity> {
        val correctionSpec = QAuditControlCorrectionEntity.auditControlCorrectionEntity

        val whereCondition = listOf(
            correctionSpec.auditControl.project.id.eq(projectId),
            correctionSpec.status.eq(AuditControlStatus.Closed),
            correctionSpec.impact.`in`(impacts),
            correctionSpec.projectModificationId.isNull(),
        ).joinWithAnd()

        return jpaQueryFactory
            .select(correctionSpec)
            .from(correctionSpec)
            .where(whereCondition)
            .fetch()
    }


    private fun fetchCorrectionsForPaymentToEc(pageable: Pageable, filter: PaymentToEcCorrectionSearchRequest) =
        jpaQueryFactory
            .select(
                accountCorrection,
                audit.project.id,
                CaseBuilder().`when`(accountCorrection.partnerReport.isNotNull())
                    .then(partnerReport.identification.projectAcronym)
                    .otherwise(ftlsPayment.projectAcronym),                                 // if linked to report
                CaseBuilder().`when`(accountCorrection.partnerReport.isNotNull())           // if linked to FTLS
                    .then(partnerReport.identification.projectIdentifier)  // if linked to report
                    .otherwise(ftlsPayment.projectCustomIdentifier),                        // if linked to FTLS
                project, // for priority axis
                audit.controllingBody,
                projectContractingMonitoring.typologyProv94,
                projectContractingMonitoring.typologyProv95,

                ecPayment.id,
                ecPayment.status,
                correctionExtensionToEc.fundAmount,
                correctionExtensionToEc.publicContribution,
                correctionExtensionToEc.correctedPublicContribution,
                correctionExtensionToEc.autoPublicContribution,
                correctionExtensionToEc.correctedAutoPublicContribution,
                correctionExtensionToEc.privateContribution,
                correctionExtensionToEc.correctedPrivateContribution,
                correctionExtensionToEc.comment,
                correctionProgrammeMeasure.scenario,

                correctionExtensionToEc.finalScoBasis,
                correctionExtensionToEc.correctedFundAmount,
                correctionExtensionToEc.totalEligibleWithoutArt94or95,
                correctionExtensionToEc.correctedTotalEligibleWithoutArt94or95,
                correctionExtensionToEc.unionContribution,
                correctionExtensionToEc.correctedUnionContribution,
            )
            .from(accountCorrection)
                .leftJoin(partnerReport)
                    .on(partnerReport.eq(accountCorrection.partnerReport))
                .leftJoin(audit)
                    .on(audit.eq(accountCorrection.auditControl))
                .leftJoin(projectLumpSum)
                    .on(projectLumpSum.eq(accountCorrection.lumpSum))
                .leftJoin(ftlsPayment)
                    .on(ftlsPayment.fund.eq(accountCorrection.programmeFund).and(ftlsPayment.projectLumpSum.eq(projectLumpSum)))
                .leftJoin(project)
                    .on(project.eq(audit.project))
                .leftJoin(correctionExtensionToEc)
                    .on(correctionExtensionToEc.correction.eq(accountCorrection))
                .leftJoin(correctionProgrammeMeasure)
                    .on(correctionProgrammeMeasure.correction.eq(accountCorrection))
                .leftJoin(projectContractingMonitoring)
                    .on(projectContractingMonitoring.projectId.eq(project.id))
                .leftJoin(ecPayment)
                    .on(ecPayment.eq(correctionExtensionToEc.paymentApplicationToEc))
            .where(
                filter.transformToWhereClause(accountCorrection, correctionProgrammeMeasure, correctionExtensionToEc)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(pageable.sort.toQueryDslOrderByForCorrection())
            .fetchResults()
            .toPaymentToEcCorrectionPageResult(pageable)

    private fun fetchCorrectionsForPaymentAccount(pageable: Pageable, filter: PaymentAccountCorrectionSearchRequest) =
        jpaQueryFactory
            .select(
                accountCorrection,
                audit.project.id,
                CaseBuilder().`when`(accountCorrection.partnerReport.isNotNull())
                    .then(partnerReport.identification.projectAcronym)
                    .otherwise(ftlsPayment.projectAcronym),                         // if linked to report
                CaseBuilder().`when`(accountCorrection.partnerReport.isNotNull())   // if linked to FTLS
                    .then(partnerReport.identification.projectIdentifier)           // if linked to report
                    .otherwise(ftlsPayment.projectCustomIdentifier),                // if linked to FTLS

                project, // for priority axis
                audit.controllingBody,

                correctionExtensionToAccount.paymentAccount.id,
                correctionExtensionToAccount.fundAmount,
                correctionExtensionToAccount.publicContribution,
                correctionExtensionToAccount.correctedPublicContribution,
                correctionExtensionToAccount.autoPublicContribution,
                correctionExtensionToAccount.correctedAutoPublicContribution,
                correctionExtensionToAccount.privateContribution,
                correctionExtensionToAccount.correctedPrivateContribution,
                correctionExtensionToAccount.comment,
                correctionProgrammeMeasure.scenario,
            )
            .from(accountCorrection)
                .leftJoin(partnerReport)
                    .on(partnerReport.eq(accountCorrection.partnerReport))
                .leftJoin(audit)
                    .on(audit.eq(accountCorrection.auditControl))
                .leftJoin(projectLumpSum)
                    .on(projectLumpSum.eq(accountCorrection.lumpSum))
                .leftJoin(ftlsPayment)
                    .on(ftlsPayment.fund.eq(accountCorrection.programmeFund).and(ftlsPayment.projectLumpSum.eq(projectLumpSum)))
                .leftJoin(project)
                    .on(project.eq(audit.project))
                .leftJoin(correctionExtensionToAccount)
                    .on(correctionExtensionToAccount.correction.eq(accountCorrection))
                .leftJoin(correctionProgrammeMeasure)
                    .on(correctionProgrammeMeasure.correction.eq(accountCorrection))
            .where(
                filter.transformToWhereClause(accountCorrection, correctionProgrammeMeasure, correctionExtensionToAccount)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(pageable.sort.toQueryDslOrderByForCorrection())
            .fetchResults()
            .toPaymentAccountCorrectionPageResult(pageable)


    private fun QueryResults<Tuple>.toPaymentAccountCorrectionPageResult(pageable: Pageable) = PageImpl(
        results.map { it: Tuple ->
            PaymentAccountCorrectionTmp(
                correctionEntity = it.get(0, AuditControlCorrectionEntity::class.java)!!,
                projectId = it.get(1, Long::class.java)!!,
                projectAcronym = it.get(2, String::class.java)!!,
                projectCustomIdentifier = it.get(3, String::class.java)!!,
                priorityAxis = it.get(4, ProjectEntity::class.java)!!.priorityPolicy!!.programmePriority!!.code,
                controllingBody = it.get(5, ControllingBody::class.java)!!,
                paymentAccountId = it.get(6, Long::class.java),

                fundAmount = it.get(7, BigDecimal::class.java)!!,
                publicContribution = it.get(8, BigDecimal::class.java)!!,
                correctedPublicContribution = it.get(9, BigDecimal::class.java)!!,
                autoPublicContribution = it.get(10, BigDecimal::class.java)!!,
                correctedAutoPublicContribution = it.get(11, BigDecimal::class.java)!!,
                privateContribution = it.get(12, BigDecimal::class.java)!!,
                correctedPrivateContribution = it.get(13, BigDecimal::class.java)!!,
                comment = it.get(14, String::class.java),
                scenario = it.get(15, ProjectCorrectionProgrammeMeasureScenario::class.java)!!,
            )
        },
        pageable,
        total,
    )

    private fun QueryResults<Tuple>.toPaymentToEcCorrectionPageResult(pageable: Pageable) = PageImpl(
        results.map { it: Tuple ->
            PaymentToEcCorrectionTmp(
                correctionEntity = it.get(0, AuditControlCorrectionEntity::class.java)!!,
                projectId = it.get(1, Long::class.java)!!,
                projectAcronym = it.get(2, String::class.java) ?: "N/A",
                projectCustomIdentifier = it.get(3, String::class.java) ?: "N/A",
                priorityAxis = it.get(4, ProjectEntity::class.java)!!.priorityPolicy!!.programmePriority!!.code,
                controllingBody = it.get(5, ControllingBody::class.java)!!,
                isProjectFlagged94Or95 = checkProjectFallsUnderArticle94Or95(
                    it.get(6, ContractingMonitoringExtendedOption::class.java),
                    it.get(7, ContractingMonitoringExtendedOption::class.java),
                    it.get(9, PaymentEcStatus::class.java),
                    it.get(19, PaymentSearchRequestScoBasis::class.java),
                ),
                paymentToEcId = it.get(8, Long::class.java),

                fundAmount = it.get(10, BigDecimal::class.java)!!,
                publicContribution = it.get(11, BigDecimal::class.java)!!,
                correctedPublicContribution = it.get(12, BigDecimal::class.java)!!,
                autoPublicContribution = it.get(13, BigDecimal::class.java)!!,
                correctedAutoPublicContribution = it.get(14, BigDecimal::class.java)!!,
                privateContribution = it.get(15, BigDecimal::class.java)!!,
                correctedPrivateContribution = it.get(16, BigDecimal::class.java)!!,
                comment = it.get(17, String::class.java),
                scenario = it.get(18, ProjectCorrectionProgrammeMeasureScenario::class.java)!!,

                correctedFundAmount = it.get(20, BigDecimal::class.java)!!,
                totalEligibleWithoutArt94or95 = it.get(21, BigDecimal::class.java)!!,
                correctedTotalEligibleWithoutArt94or95 = it.get(22, BigDecimal::class.java)!!,
                unionContribution = it.get(23, BigDecimal::class.java)!!,
                correctedUnionContribution = it.get(24, BigDecimal::class.java)!!,
            )
        },
        pageable,
        total,
    )

    private fun checkProjectFallsUnderArticle94Or95(
        flaggedArticle94: ContractingMonitoringExtendedOption?,
        flaggedArticle95: ContractingMonitoringExtendedOption?,
        paymentToEcStatus: PaymentEcStatus?,
        finalScoFlag: PaymentSearchRequestScoBasis?
    ): Boolean {
        if (paymentToEcStatus?.isFinished() == true)
            return finalScoFlag == PaymentSearchRequestScoBasis.FallsUnderArticle94Or95

        return (flaggedArticle94 ?: ContractingMonitoringExtendedOption.No).isYes()
                || (flaggedArticle95 ?: ContractingMonitoringExtendedOption.No).isYes()
    }

}
