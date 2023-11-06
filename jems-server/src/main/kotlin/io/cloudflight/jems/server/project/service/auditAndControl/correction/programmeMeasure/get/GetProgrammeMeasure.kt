package io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.get

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.ProjectCorrectionProgrammeMeasurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProgrammeMeasure(
    private val programmeMeasurePersistence: ProjectCorrectionProgrammeMeasurePersistence,
) : GetProgrammeMeasureInteractor {

    @CanViewProjectCorrection
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProgrammeMeasureException::class)
    override fun get(correctionId: Long): ProjectCorrectionProgrammeMeasure =
        programmeMeasurePersistence.getProgrammeMeasure(correctionId = correctionId)
}
