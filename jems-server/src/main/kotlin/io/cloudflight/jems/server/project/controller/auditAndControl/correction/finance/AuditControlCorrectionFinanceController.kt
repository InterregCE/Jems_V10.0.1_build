package io.cloudflight.jems.server.project.controller.auditAndControl.correction.finance

import io.cloudflight.jems.api.project.auditAndControl.corrections.finance.ProjectAuditControlCorrectionFinancialDescriptionApi
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionUpdateDTO
import io.cloudflight.jems.server.project.service.auditAndControl.correction.finance.getProjectCorrectionFinancialDescription.GetProjectCorrectionFinancialDescriptionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.finance.updateProjectCorrectionFinancialDescription.UpdateProjectCorrectionFinancialDescriptionInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class AuditControlCorrectionFinanceController(
    private val getFinance: GetProjectCorrectionFinancialDescriptionInteractor,
    private val updateFinance: UpdateProjectCorrectionFinancialDescriptionInteractor
): ProjectAuditControlCorrectionFinancialDescriptionApi {

    override fun getCorrectionFinancialDescription(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): ProjectCorrectionFinancialDescriptionDTO =
        getFinance.getCorrectionFinancialDescription(correctionId).toDto()

    override fun updateCorrectionFinancialDescription(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long,
        financialDescriptionUpdate: ProjectCorrectionFinancialDescriptionUpdateDTO,
    ): ProjectCorrectionFinancialDescriptionDTO =
        updateFinance.updateCorrectionFinancialDescription(correctionId, financialDescriptionUpdate.toModel()).toDto()

}
