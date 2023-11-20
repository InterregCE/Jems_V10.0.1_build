package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.payments.repository.regular.joinWithAnd
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AvailableCorrectionsForPayment
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.CorrectionImpactAction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class AuditControlCorrectionPersistenceProvider(
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
    private val reportExpenditureRepository: ProjectPartnerReportExpenditureRepository,
    private val projectStatusHistoryRepository: ProjectStatusHistoryRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : AuditControlCorrectionPersistence {

    @Transactional(readOnly = true)
    override fun getProjectIdForCorrection(correctionId: Long): Long =
        auditControlCorrectionRepository.getById(correctionId).auditControl.project.id

    @Transactional(readOnly = true)
    override fun getAllCorrectionsByAuditControlId(
        auditControlId: Long,
        pageable: Pageable
    ): Page<AuditControlCorrection> =
        auditControlCorrectionRepository.findAllByAuditControlId(auditControlId, pageable)
            .map { it.toSimpleModel() }


    @Transactional(readOnly = true)
    override fun getPreviousClosedCorrections(correctionId: Long): List<AuditControlCorrection> {
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
            entity.partnerReport = partnerReportRepository.getById(data.partnerReportId)

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
    override fun getAllIdsByProjectId(projectId: Long): Set<Long> =
        auditControlCorrectionRepository.findAllByAuditControlProjectId(projectId = projectId)

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

}
