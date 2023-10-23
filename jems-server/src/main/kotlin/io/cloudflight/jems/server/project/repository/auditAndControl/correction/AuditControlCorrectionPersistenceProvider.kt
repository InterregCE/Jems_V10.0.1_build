package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class AuditControlCorrectionPersistenceProvider(
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val auditControlRepository: AuditControlRepository
) : AuditControlCorrectionPersistence {

    @Transactional
    override fun saveCorrection(correction: ProjectAuditControlCorrection): ProjectAuditControlCorrection {
        return auditControlCorrectionRepository.save(correction.toEntity(auditControlResolver = {
            auditControlRepository.getById(
                it
            )
        })).toModel()
    }

    @Transactional(readOnly = true)
    override fun getAllCorrectionsByAuditControlId(
        auditControlId: Long,
        pageable: Pageable
    ): Page<ProjectAuditControlCorrection> =
        auditControlCorrectionRepository.findAllByAuditControlEntityId(auditControlId, pageable)
            .map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getByCorrectionId(correctionId: Long): ProjectAuditControlCorrection =
        auditControlCorrectionRepository.getById(correctionId).toModel()

    @Transactional(readOnly = true)
    override fun getExtendedByCorrectionId(correctionId: Long): ProjectAuditControlCorrectionExtended =
        auditControlCorrectionRepository.getById(correctionId).toExtendedModel()


    @Transactional(readOnly = true)
    override fun getLastUsedOrderNr(auditControlId: Long): Int? =
        auditControlCorrectionRepository.findFirstByAuditControlEntityIdOrderByOrderNrDesc(auditControlId)?.orderNr

    @Transactional(readOnly = true)
    override fun getLastCorrectionIdByAuditControlId(auditControlId: Long): Long? =
        auditControlCorrectionRepository.findFirstByAuditControlEntityIdOrderByOrderNrDesc(auditControlId)?.id

    @Transactional
    override fun deleteCorrectionById(id: Long) =
        auditControlCorrectionRepository.deleteById(id)

}
