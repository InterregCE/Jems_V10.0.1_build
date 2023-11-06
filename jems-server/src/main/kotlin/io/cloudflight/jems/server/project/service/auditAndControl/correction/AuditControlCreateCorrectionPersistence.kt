package io.cloudflight.jems.server.project.service.auditAndControl.correction

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection

interface AuditControlCreateCorrectionPersistence {

    fun createCorrection(correction: ProjectAuditControlCorrection): ProjectAuditControlCorrection

}
