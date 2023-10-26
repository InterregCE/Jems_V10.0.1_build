package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionIdentificationEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.identification.CorrectionIdentificationRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCreateCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class CreateCorrectionPersistenceProvider(
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val auditControlRepository: AuditControlRepository,
    private val auditControlCorrectionIdentificationRepository: CorrectionIdentificationRepository
) : AuditControlCreateCorrectionPersistence {

    @Transactional
    override fun createCorrection(correction: ProjectAuditControlCorrection): ProjectAuditControlCorrection {
        val correctionEntity = persistCorrection(correction)
        persistIdentification(correctionEntity)

        return correctionEntity.toModel()
    }

    private fun persistCorrection(correction: ProjectAuditControlCorrection): ProjectAuditControlCorrectionEntity =
        auditControlCorrectionRepository.save(correction.toEntity(auditControlResolver = {
            auditControlRepository.getById(
                it
            )
        }))


    private fun persistIdentification(correction: ProjectAuditControlCorrectionEntity) {
        auditControlCorrectionIdentificationRepository.save(
            ProjectCorrectionIdentificationEntity(
                correctionEntity = correction,
                followUpOfCorrectionId = null,
                correctionFollowUpType = CorrectionFollowUpType.No,
                repaymentFrom = null,
                lateRepaymentTo = null,
                partnerId = null,
                partnerReportId = null,
                programmeFundId = null
            )
        )
    }

}
