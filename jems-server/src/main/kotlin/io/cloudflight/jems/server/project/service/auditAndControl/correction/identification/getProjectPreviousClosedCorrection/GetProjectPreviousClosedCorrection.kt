package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectAuditAndControl
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPreviousClosedCorrection(
    private val correctionPersistence: AuditControlCorrectionPersistence,
): GetProjectPreviousClosedCorrectionsInteractor {

    @CanViewProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(GetProjectPreviousClosedCorrectionsException::class)
    override fun getProjectPreviousClosedCorrections(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): List<ProjectAuditControlCorrection> =
        correctionPersistence.getPreviousClosedCorrections(auditControlId, correctionId)

}
