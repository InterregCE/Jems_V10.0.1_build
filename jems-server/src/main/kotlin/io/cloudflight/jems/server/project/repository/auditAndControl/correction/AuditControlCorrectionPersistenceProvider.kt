package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.payments.entity.QPaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionSearchRequest
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionTmp
import io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection.toModel
import io.cloudflight.jems.server.payments.repository.regular.joinWithAnd
import io.cloudflight.jems.server.programme.entity.QProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.QProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionMeasureEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlEntity
import io.cloudflight.jems.server.project.entity.contracting.QProjectContractingMonitoringEntity
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

    @Transactional(readOnly = true)
    override fun getProjectIdForCorrection(correctionId: Long): Long =
        auditControlCorrectionRepository.getById(correctionId).auditControl.project.id

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
        val currentCorrection = auditControlCorrectionRepository.getById(correctionId)
        val auditControl = currentCorrection.auditControl

        return auditControlCorrectionRepository.getAllByAuditControlAndStatusAndOrderNrBefore(
            auditControl = auditControl,
            status = AuditControlStatus.Closed,
            orderNr = currentCorrection.orderNr,
        ).map { it.toSimpleModel() }
    }

    @Transactional(readOnly = true)
    override fun getByCorrectionId(correctionId: Long): AuditControlCorrectionDetail =
        auditControlCorrectionRepository.getById(correctionId).toModel()

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
            entity.partnerReport = data.partnerReportId?.let { partnerReportRepository.getById(it) }

        if (entity.lumpSum?.id?.orderNr != data.lumpSumOrderNr) {
            entity.lumpSum = data.lumpSumOrderNr?.let { projectLumpSumRepository.getByIdProjectIdAndIdOrderNr(entity.auditControl.project.id, it) }
            entity.lumpSumPartnerId = data.partnerId
        }

        if (entity.programmeFund?.id != data.programmeFundId)
            entity.programmeFund = programmeFundRepository.getById(data.programmeFundId)

        if (entity.followUpOfCorrection?.id != data.followUpOfCorrectionId)
            entity.followUpOfCorrection = data.followUpOfCorrectionId?.let { auditControlCorrectionRepository.getById(it) }

        entity.followUpOfCorrectionType = data.correctionFollowUpType
        entity.repaymentDate = data.repaymentFrom
        entity.lateRepayment = data.lateRepaymentTo

        entity.procurementId = data.procurementId
        entity.costCategory = data.costCategory
        entity.expenditure =
            if (data.expenditureId != null) reportExpenditureRepository.getById(data.expenditureId) else null

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
            .groupBy { it.partnerReport!!.partnerId }
            .map {
                AvailableCorrectionsForPayment(
                    partnerId = it.key,
                    corrections = it.value.map { it.toSimpleModel() }
                )
            }

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


    private fun fetchCorrections(pageable: Pageable, filter: PaymentToEcCorrectionSearchRequest): Page<PaymentToEcCorrectionTmp> {
        val specCorrection = QAuditControlCorrectionEntity.auditControlCorrectionEntity
        val specProjectAuditControl = QAuditControlEntity.auditControlEntity
        val specProjectEntity = QProjectEntity.projectEntity
        val specPaymentToEcCorrectionExtensionEntity =
            QPaymentToEcCorrectionExtensionEntity.paymentToEcCorrectionExtensionEntity
        val specCorrectionProgrammeMeasure =
            QAuditControlCorrectionMeasureEntity.auditControlCorrectionMeasureEntity
        val specProgrammePriorityEntity = QProgrammePriorityEntity.programmePriorityEntity
        val specProgrammeSpecificObjectiveEntity = QProgrammeSpecificObjectiveEntity.programmeSpecificObjectiveEntity
        val specProjectContractingMonitoringEntity = QProjectContractingMonitoringEntity.projectContractingMonitoringEntity

        val results = jpaQueryFactory
            .select(
                specCorrection,
                specProjectAuditControl,
                specProjectEntity.id,
                specProjectEntity.acronym,
                specProjectEntity.customIdentifier,
                specProgrammePriorityEntity.code,
                specProjectAuditControl.controllingBody,
                specProjectContractingMonitoringEntity.typologyProv94,
                specProjectContractingMonitoringEntity.typologyProv95,

                specPaymentToEcCorrectionExtensionEntity.paymentApplicationToEc.id,
                specPaymentToEcCorrectionExtensionEntity.fundAmount,
                specPaymentToEcCorrectionExtensionEntity.publicContribution,
                specPaymentToEcCorrectionExtensionEntity.correctedPublicContribution,
                specPaymentToEcCorrectionExtensionEntity.autoPublicContribution,
                specPaymentToEcCorrectionExtensionEntity.correctedAutoPublicContribution,
                specPaymentToEcCorrectionExtensionEntity.privateContribution,
                specPaymentToEcCorrectionExtensionEntity.correctedPrivateContribution,
                specPaymentToEcCorrectionExtensionEntity.comment,
                specCorrectionProgrammeMeasure.scenario
            )
            .from(specCorrection)
            .leftJoin(specProjectAuditControl)
                .on(specProjectAuditControl.id.eq(specCorrection.auditControl.id))
            .leftJoin(specProjectEntity)
                .on(specProjectEntity.id.eq(specProjectAuditControl.project.id))
            .leftJoin(specProgrammeSpecificObjectiveEntity)
                .on(specProgrammeSpecificObjectiveEntity.programmeObjectivePolicy.eq(specProjectEntity.priorityPolicy.programmeObjectivePolicy))
            .leftJoin(specProgrammePriorityEntity)
                .on(specProgrammePriorityEntity.id.eq(specProgrammeSpecificObjectiveEntity.programmePriority.id))
            .leftJoin(specPaymentToEcCorrectionExtensionEntity)
                .on(specPaymentToEcCorrectionExtensionEntity.correction.id.eq(specCorrection.id))
            .join(specCorrectionProgrammeMeasure)
                .on(specCorrectionProgrammeMeasure.correctionId.eq(specCorrection.id))
            .leftJoin(specProjectContractingMonitoringEntity)
                .on(specProjectContractingMonitoringEntity.projectId.eq(specProjectEntity.id))
            .where(
                filter.transformToWhereClause(specCorrection, specCorrectionProgrammeMeasure, specPaymentToEcCorrectionExtensionEntity)
            ).apply{
                if (pageable.isPaged){
                    this.offset(pageable.offset)
                        .limit(pageable.pageSize.toLong())
                        .orderBy(pageable.sort.toQueryDslOrderByForCorrection())
                }
            }
            .fetch()

        return results.toPaymentToEcCorrectionPageResult(pageable)
    }

    @Transactional(readOnly = true)
    override fun getCorrectionsLinkedToPaymentToEc(
        pageable: Pageable,
        filter: PaymentToEcCorrectionSearchRequest
    ): Page<PaymentToEcCorrectionLinking> =
        fetchCorrections(
            pageable,
            filter
        ).map {
            it.toModel(
                partnerContribution = it.publicContribution.add(it.autoPublicContribution).add(it.privateContribution)
            )
        }

    @Transactional(readOnly = true)
    override fun existsByProcurementId(procurementId: Long): Boolean =
        auditControlCorrectionRepository.existsByProcurementId(procurementId)

    fun List<Tuple>.toPaymentToEcCorrectionPageResult(pageable: Pageable) = PageImpl(
        this.map { it: Tuple ->
            PaymentToEcCorrectionTmp(
                correctionEntity = it.get(0, AuditControlCorrectionEntity::class.java)!!,
                projectId = it.get(2, Long::class.java)!!,
                projectAcronym = it.get(3, String::class.java)!!,
                projectCustomIdentifier = it.get(4, String::class.java)!!,
                priorityAxis = it.get(5, String::class.java),
                controllingBody = it.get(6, ControllingBody::class.java)!!,
                isProjectFlagged94Or95 = checkProjectFallsUnderArticle94Or95(
                    it.get(
                        7,
                        ContractingMonitoringExtendedOption::class.java
                    ), it.get(8, ContractingMonitoringExtendedOption::class.java)
                ),
                paymentToEcId = it.get(9, Long::class.java),

                fundAmount = it.get(10, BigDecimal::class.java)!!,
                publicContribution = it.get(11, BigDecimal::class.java)!!,
                correctedPublicContribution = it.get(12, BigDecimal::class.java)!!,
                autoPublicContribution = it.get(13, BigDecimal::class.java)!!,
                correctedAutoPublicContribution = it.get(14, BigDecimal::class.java)!!,
                privateContribution = it.get(15, BigDecimal::class.java)!!,
                correctedPrivateContribution = it.get(16, BigDecimal::class.java)!!,
                comment = it.get(17, String::class.java),
                scenario = it.get(18, ProjectCorrectionProgrammeMeasureScenario::class.java)!!,
            )
        },
        pageable,
        this.size.toLong(),
    )


    private fun checkProjectFallsUnderArticle94Or95(
        flaggedArticle94: ContractingMonitoringExtendedOption?,
        flaggedArticle95: ContractingMonitoringExtendedOption?
    ) =
        if (flaggedArticle94 == null || flaggedArticle95 == null)
            false
        else flaggedArticle94.isYes() || flaggedArticle95.isYes()


}
