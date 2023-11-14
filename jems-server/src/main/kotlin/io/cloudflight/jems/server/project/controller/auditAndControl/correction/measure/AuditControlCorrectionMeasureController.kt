package io.cloudflight.jems.server.project.controller.auditAndControl.correction.measure

import io.cloudflight.jems.api.project.auditAndControl.corrections.measure.ProjectAuditControlCorrectionProgrammeMeasureApi
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureUpdateDTO
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.get.GetProgrammeMeasureInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.update.UpdateProgrammeMeasureInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class AuditControlCorrectionMeasureController(
    private val getProgrammeMeasure: GetProgrammeMeasureInteractor,
    private val updateProgrammeMeasure: UpdateProgrammeMeasureInteractor,
) : ProjectAuditControlCorrectionProgrammeMeasureApi {

    override fun getProgrammeMeasure(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): ProjectCorrectionProgrammeMeasureDTO =
        getProgrammeMeasure.get(correctionId).toDto()

    override fun updateProgrammeMeasure(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long,
        programmeMeasure: ProjectCorrectionProgrammeMeasureUpdateDTO
    ): ProjectCorrectionProgrammeMeasureDTO =
        updateProgrammeMeasure.update(correctionId, programmeMeasure.toModel()).toDto()
}
