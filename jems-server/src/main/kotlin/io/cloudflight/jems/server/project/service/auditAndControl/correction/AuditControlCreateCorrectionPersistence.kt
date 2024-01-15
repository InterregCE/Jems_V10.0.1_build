package io.cloudflight.jems.server.project.service.auditAndControl.correction

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail

interface AuditControlCreateCorrectionPersistence {

    fun createCorrection(auditControlId: Long, correction: AuditControlCorrectionCreate): AuditControlCorrectionDetail

}
