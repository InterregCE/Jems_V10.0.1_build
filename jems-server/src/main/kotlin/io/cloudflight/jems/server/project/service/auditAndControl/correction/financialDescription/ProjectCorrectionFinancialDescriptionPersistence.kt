package io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription

import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescriptionUpdate

interface ProjectCorrectionFinancialDescriptionPersistence {

    fun getCorrectionFinancialDescription(correctionId: Long): ProjectCorrectionFinancialDescription

    fun updateCorrectionFinancialDescription(
        correctionId: Long,
        financialDescription: ProjectCorrectionFinancialDescriptionUpdate
    ) : ProjectCorrectionFinancialDescription
}
