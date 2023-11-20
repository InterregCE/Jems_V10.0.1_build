package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForModification

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection

interface GetAvailableCorrectionsForModificationInteractor {

    fun getAvailableCorrections(projectId: Long): List<AuditControlCorrection>
}
