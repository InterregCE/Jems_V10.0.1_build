package io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.updateProjectCorrectionFinancialDescription

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectAuditAndControl
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.ProjectCorrectionFinancialDescriptionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescriptionUpdate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectProjectCorrectionFinancialDescription(
    private val financialDescriptionPersistence: ProjectCorrectionFinancialDescriptionPersistence,
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val auditControlPersistence: AuditControlPersistence,
): UpdateProjectCorrectionFinancialDescriptionInteractor {

    @CanEditProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(UpdateCorrectionFinancialDescriptionException::class)
    override fun updateCorrectionFinancialDescription(
        projectId: Long,
        controlId: Long,
        correctionId: Long,
        correctionFinancialDescriptionUpdate: ProjectCorrectionFinancialDescriptionUpdate
    ): ProjectCorrectionFinancialDescription {
        validateControlStatus(auditControlPersistence.getByIdAndProjectId(controlId, projectId))
        validateCorrectionStatus(correctionPersistence.getByCorrectionId(correctionId))
        return financialDescriptionPersistence.updateCorrectionFinancialDescription(correctionId, correctionFinancialDescriptionUpdate)
    }

    private fun validateControlStatus(auditControl: ProjectAuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlIsInStatusClosedException()
    }

    private fun validateCorrectionStatus(correction: ProjectAuditControlCorrection) {
        if (correction.status.isClosed())
            throw CorrectionIsInStatusClosedException()
    }
}
