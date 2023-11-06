package io.cloudflight.jems.server.project.service.auditAndControl.correction.getProjectAuditCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectAuditControlCorrection(
    private val correctionPersistence: AuditControlCorrectionPersistence,
): GetProjectAuditControlCorrectionInteractor {

    @CanViewProjectCorrection
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectAuditControlCorrectionException::class)
    override fun getProjectAuditCorrection(
        correctionId: Long
    ): ProjectAuditControlCorrectionExtended =
        correctionPersistence.getExtendedByCorrectionId(correctionId)

}
