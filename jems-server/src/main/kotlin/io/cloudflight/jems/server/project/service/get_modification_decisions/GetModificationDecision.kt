package io.cloudflight.jems.server.project.service.get_modification_decisions

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectModifications
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.model.ProjectModificationDecision
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetModificationDecision(
    private val persistence: ProjectWorkflowPersistence,
    private val correctionPersistence: AuditControlCorrectionPersistence,
) : GetModificationDecisionsInteractor {

    @CanRetrieveProjectModifications
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetModificationDecisionExceptions::class)
    override fun getModificationDecisions(projectId: Long): List<ProjectModificationDecision> {
        val modificationCorrections = correctionPersistence.getCorrectionsForModificationDecisions(projectId = projectId)

        return this.persistence.getModificationDecisions(projectId)
            .toModificationDecision(correctionResolver = { modificationCorrections[it] })
    }

    fun List<ProjectStatus>.toModificationDecision(correctionResolver: (Long) -> List<AuditControlCorrection>?): List<ProjectModificationDecision> =
        map {
            ProjectModificationDecision(
                projectStatus = it,
                corrections = correctionResolver(it.id!!) ?: emptyList()
            )
        }

}
