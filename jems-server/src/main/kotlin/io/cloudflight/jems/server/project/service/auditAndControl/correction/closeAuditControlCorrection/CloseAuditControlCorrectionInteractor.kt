package io.cloudflight.jems.server.project.service.auditAndControl.correction.closeAuditControlCorrection

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus

interface CloseAuditControlCorrectionInteractor {

    fun closeCorrection(correctionId: Long): AuditControlStatus

}
