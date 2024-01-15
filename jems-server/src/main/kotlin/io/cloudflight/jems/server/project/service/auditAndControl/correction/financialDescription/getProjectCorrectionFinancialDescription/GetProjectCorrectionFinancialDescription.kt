package io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.getProjectCorrectionFinancialDescription

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.AuditControlCorrectionFinancePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectCorrectionFinancialDescription(
    private val financialDescriptionPersistence: AuditControlCorrectionFinancePersistence,
) : GetProjectCorrectionFinancialDescriptionInteractor {

    @CanViewAuditControlCorrection
    @Transactional
    @ExceptionWrapper(GetProjectCorrectionFinancialDescriptionException::class)
    override fun getCorrectionFinancialDescription(correctionId: Long): ProjectCorrectionFinancialDescription =
        financialDescriptionPersistence.getCorrectionFinancialDescription(correctionId)

}
