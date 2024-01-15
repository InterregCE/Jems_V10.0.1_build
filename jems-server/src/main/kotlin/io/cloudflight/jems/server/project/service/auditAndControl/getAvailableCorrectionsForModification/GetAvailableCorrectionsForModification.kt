package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForModification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectModifications
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableCorrectionsForModification(
    private val correctionsPersistence: AuditControlCorrectionPersistence
) : GetAvailableCorrectionsForModificationInteractor {

    @CanRetrieveProjectModifications
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableCorrectionsForModificationException::class)
    override fun getAvailableCorrections(projectId: Long): List<AuditControlCorrection> =
        correctionsPersistence.getAvailableCorrectionsForModification(projectId = projectId)
}
