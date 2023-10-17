package io.cloudflight.jems.server.project.service.auditAndControl.correction.getProjectAuditCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectAuditAndControl
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectAuditControlCorrection(
    private val correctionPersistence: AuditControlCorrectionPersistence,
): GetProjectAuditControlCorrectionInteractor {

    @CanViewProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(GetProjectAuditControlCorrectionException::class)
    override fun getProjectAuditCorrection(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): ProjectAuditControlCorrectionExtended =
        correctionPersistence.getExtendedByCorrectionId(correctionId)

}
