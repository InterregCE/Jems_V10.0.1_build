package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class AuditControlCorrectionPersistenceProvider(
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
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

        return entity.toModel()
    }

}
