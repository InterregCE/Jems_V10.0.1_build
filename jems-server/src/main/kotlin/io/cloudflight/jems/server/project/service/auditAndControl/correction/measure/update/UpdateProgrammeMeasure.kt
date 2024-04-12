package io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.update

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasureUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.AuditControlCorrectionMeasurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProgrammeMeasure(
    private val programmeMeasurePersistence: AuditControlCorrectionMeasurePersistence,
    private val generalValidatorService: GeneralValidatorService,
) : UpdateProgrammeMeasureInteractor {

    @CanEditAuditControlCorrection
    @Transactional
    @ExceptionWrapper(UpdateProgrammeMeasureException::class)
    override fun update(
        correctionId: Long,
        programmeMeasure: AuditControlCorrectionMeasureUpdate
    ): AuditControlCorrectionMeasure {

        validateProgrammeMeasure(programmeMeasure)

        return programmeMeasurePersistence.updateProgrammeMeasure(
            correctionId = correctionId,
            programmeMeasure = programmeMeasure
        )
    }

    fun validateProgrammeMeasure(programmeMeasure: AuditControlCorrectionMeasureUpdate) {
        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.maxLength(programmeMeasure.comment, 2000, "comment")
        )
    }
}
