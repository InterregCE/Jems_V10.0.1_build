package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate

interface UpdateAuditControlCorrectionInteractor {

    fun updateCorrection(
        correctionId: Long,
        data: AuditControlCorrectionUpdate
    ): AuditControlCorrectionDetail

}
