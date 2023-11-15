package io.cloudflight.jems.server.project.controller.auditAndControl.correction.impact

import io.cloudflight.jems.api.project.auditAndControl.corrections.impact.AuditControlCorrectionImpactApi
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionUpdateDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.AuditControlCorrectionImpactDTO
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.getProjectCorrectionFinancialDescription.GetProjectCorrectionFinancialDescriptionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.updateProjectCorrectionFinancialDescription.UpdateProjectCorrectionFinancialDescriptionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.impact.updateImpact.UpdateAuditControlCorrectionImpactInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class AuditControlCorrectionImpactController(
    private val updateImpact: UpdateAuditControlCorrectionImpactInteractor,
): AuditControlCorrectionImpactApi {

    override fun updateImpact(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long,
        impact: AuditControlCorrectionImpactDTO
    ): AuditControlCorrectionImpactDTO =
        updateImpact.update(correctionId, impact.toModel()).toDto()

}
