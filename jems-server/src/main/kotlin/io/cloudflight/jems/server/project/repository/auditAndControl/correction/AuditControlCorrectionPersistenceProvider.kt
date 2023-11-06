package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class AuditControlCorrectionPersistenceProvider(
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
) : AuditControlCorrectionPersistence {

    @Transactional(readOnly = true)
    override fun getAllCorrectionsByAuditControlId(
        auditControlId: Long,
        pageable: Pageable
    ): Page<ProjectAuditControlCorrection> =
        auditControlCorrectionRepository.findAllByAuditControlEntityId(auditControlId, pageable)
            .map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getPreviousClosedCorrections(auditControlId: Long, correctionId: Long): List<ProjectAuditControlCorrection> {
        val currentCorrection = auditControlCorrectionRepository.getById(correctionId)

        return auditControlCorrectionRepository.getAllByAuditControlEntityIdAndStatusAndOrderNrBefore(
            auditControlId,
            CorrectionStatus.Closed,
            currentCorrection.orderNr
        ).map { it.toModel() }
    }


    @Transactional(readOnly = true)
    override fun getByCorrectionId(correctionId: Long): ProjectAuditControlCorrection =
        auditControlCorrectionRepository.getById(correctionId).toModel()

    @Transactional(readOnly = true)
    override fun getExtendedByCorrectionId(correctionId: Long): ProjectAuditControlCorrectionExtended =
        auditControlCorrectionRepository.getById(correctionId).toExtendedModel()


    @Transactional(readOnly = true)
    override fun getLastUsedOrderNr(auditControlId: Long): Int? =
        auditControlCorrectionRepository.findFirstByAuditControlEntityIdOrderByOrderNrDesc(auditControlId)?.orderNr

    @Transactional
    override fun deleteCorrectionById(id: Long) =
        auditControlCorrectionRepository.deleteById(id)

    @Transactional
    override fun closeCorrection(correctionId: Long): ProjectAuditControlCorrection =
        auditControlCorrectionRepository.getById(correctionId).apply {
            status = CorrectionStatus.Closed
        }.toModel()

    @Transactional(readOnly = true)
    override fun getOngoingCorrectionsByAuditControlId(auditControlId: Long): List<ProjectAuditControlCorrection> =
        auditControlCorrectionRepository.getAllByAuditControlEntityIdAndStatus(auditControlId, CorrectionStatus.Ongoing)
            .map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getLastCorrectionOngoingId(auditControlId: Long): Long? =
        auditControlCorrectionRepository.getFirstByAuditControlEntityIdAndStatusOrderByOrderNrDesc(
            auditControlId,
            CorrectionStatus.Ongoing
        )?.id

}
