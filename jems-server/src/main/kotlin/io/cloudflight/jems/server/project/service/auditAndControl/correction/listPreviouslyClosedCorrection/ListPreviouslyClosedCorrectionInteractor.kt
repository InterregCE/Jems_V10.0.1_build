package io.cloudflight.jems.server.project.service.auditAndControl.correction.listPreviouslyClosedCorrection

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection

interface ListPreviouslyClosedCorrectionInteractor {

    fun getClosedCorrectionsBefore(correctionId: Long): List<AuditControlCorrection>

}
