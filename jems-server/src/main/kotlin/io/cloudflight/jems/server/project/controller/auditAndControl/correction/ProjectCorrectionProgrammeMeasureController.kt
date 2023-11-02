package io.cloudflight.jems.server.project.controller.auditAndControl.correction

import io.cloudflight.jems.api.project.auditAndControl.corrections.ProjectCorrectionProgrammeMeasureApi
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionProgrammeMeasureDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionProgrammeMeasureUpdateDTO
import io.cloudflight.jems.server.project.controller.auditAndControl.toDto
import io.cloudflight.jems.server.project.controller.auditAndControl.toModel
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.get.GetProgrammeMeasureInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.update.UpdateProgrammeMeasureInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectCorrectionProgrammeMeasureController(
    private val getProgrammeMeasure: GetProgrammeMeasureInteractor,
    private val updateProgrammeMeasure: UpdateProgrammeMeasureInteractor,
) : ProjectCorrectionProgrammeMeasureApi {

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
