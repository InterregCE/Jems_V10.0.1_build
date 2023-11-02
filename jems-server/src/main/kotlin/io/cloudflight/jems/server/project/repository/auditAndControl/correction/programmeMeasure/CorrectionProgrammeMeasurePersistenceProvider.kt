package io.cloudflight.jems.server.project.repository.auditAndControl.correction.programmeMeasure

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.ProjectCorrectionProgrammeMeasurePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CorrectionProgrammeMeasurePersistenceProvider(
    private val programmeMeasureRepository: CorrectionProgrammeMeasureRepository,
) : ProjectCorrectionProgrammeMeasurePersistence {

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
