package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.getAuditControlCorrection

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail

interface GetAuditControlCorrectionInteractor {

    fun getCorrection(correctionId: Long): AuditControlCorrectionDetail

}
