package io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.getProjectCorrectionFinancialDescription

import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription

interface GetProjectCorrectionFinancialDescriptionInteractor {

    fun getCorrectionFinancialDescription(correctionId: Long): ProjectCorrectionFinancialDescription

}
