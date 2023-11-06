package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.ProjectCorrectionIdentificationPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectCorrectionIdentification(
    private val correctionIdentificationPersistence: ProjectCorrectionIdentificationPersistence,
): GetProjectCorrectionIdentificationInteractor {

    @CanViewProjectCorrection
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPreviousClosedCorrectionsException::class)
    override fun getProjectCorrectionIdentification(
        correctionId: Long
    ): ProjectCorrectionIdentification =
        correctionIdentificationPersistence.getCorrectionIdentification(correctionId)

}
