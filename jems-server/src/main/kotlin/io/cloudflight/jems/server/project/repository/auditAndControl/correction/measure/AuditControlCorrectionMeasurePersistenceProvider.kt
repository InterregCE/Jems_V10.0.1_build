package io.cloudflight.jems.server.project.repository.auditAndControl.correction.measure

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.AuditControlCorrectionMeasurePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class AuditControlCorrectionMeasurePersistenceProvider(
    private val programmeMeasureRepository: CorrectionProgrammeMeasureRepository,
) : AuditControlCorrectionMeasurePersistence {

    @Transactional(readOnly = true)
    override fun getProgrammeMeasure(correctionId: Long): ProjectCorrectionProgrammeMeasure =
        programmeMeasureRepository.getByCorrectionId(correctionId = correctionId).toModel()

    @Transactional
    override fun updateProgrammeMeasure(
        correctionId: Long,
        programmeMeasure: ProjectCorrectionProgrammeMeasureUpdate
    ): ProjectCorrectionProgrammeMeasure =
        programmeMeasureRepository.getByCorrectionId(correctionId = correctionId).apply {
            scenario = programmeMeasure.scenario
            comment = programmeMeasure.comment
        }.toModel()

}
