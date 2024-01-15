package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.createAuditControlCorrection

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType

interface CreateAuditControlCorrectionInteractor {

    fun createCorrection(auditControlId: Long, type: AuditControlCorrectionType): AuditControlCorrectionDetail

}
