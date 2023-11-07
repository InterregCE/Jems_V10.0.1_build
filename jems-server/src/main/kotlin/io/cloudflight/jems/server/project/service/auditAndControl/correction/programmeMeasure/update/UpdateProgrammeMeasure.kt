package io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.update

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditProjectCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.ProjectCorrectionProgrammeMeasurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProgrammeMeasure(
    private val programmeMeasurePersistence: ProjectCorrectionProgrammeMeasurePersistence,
    private val generalValidatorService: GeneralValidatorService,
) : UpdateProgrammeMeasureInteractor {

    @CanEditProjectCorrection
    @Transactional
    @ExceptionWrapper(UpdateProgrammeMeasureException::class)
    override fun update(
        correctionId: Long,
        programmeMeasure: ProjectCorrectionProgrammeMeasureUpdate
    ): ProjectCorrectionProgrammeMeasure {

        validateProgrammeMeasure(programmeMeasure)

        return programmeMeasurePersistence.updateProgrammeMeasure(
            correctionId = correctionId,
            programmeMeasure = programmeMeasure
        )
    }

    fun validateProgrammeMeasure(programmeMeasure: ProjectCorrectionProgrammeMeasureUpdate) {
        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.maxLength(programmeMeasure.comment, 2000, "comment")
        )
    }
}
