package io.cloudflight.jems.server.project.service.lumpsum.update_project_lump_sums

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectLumpSums(
    private val persistence: ProjectLumpSumPersistence,
    private val projectPersistence: ProjectPersistence,
) : UpdateProjectLumpSumsInteractor {

    companion object {
        private const val MAX_ALLOWED_PROJECT_LUMP_SUMS = 50
    }

    @Transactional
    @CanUpdateProject
    override fun updateLumpSums(projectId: Long, lumpSums: List<ProjectLumpSum>): List<ProjectLumpSum> {
        validateMaxAllowedSize(lumpSums)
        validateWrongSplitting(lumpSums, projectPersistence.getProjectCallSettingsForProject(projectId))
        validatePeriods(lumpSums, projectPersistence.getProject(projectId))

        return persistence.updateLumpSums(projectId, lumpSums)
    }

    private fun ProgrammeLumpSum?.isLumpSumNotSplittable() =
        if (this != null)
            !this.splittingAllowed
        else
            throw ResourceNotFoundException("programmeLumpSum")

    private fun validateMaxAllowedSize(lumpSums: List<ProjectLumpSum>) {
        if (lumpSums.size > MAX_ALLOWED_PROJECT_LUMP_SUMS)
            throw I18nValidationException(i18nKey = "project.lumpSum.max.allowed.reached")
    }

    private fun validateWrongSplitting(lumpSums: List<ProjectLumpSum>, settings: ProjectCallSettings) {
        val lumpSumsById = settings.lumpSums.associateBy { it.id!! }

        if (lumpSums.any {
                it.lumpSumContributions.size > 1 && lumpSumsById[it.programmeLumpSumId].isLumpSumNotSplittable()
            })
            throw I18nValidationException(i18nKey = "project.lumpSum.splitting.not.allowed")
    }

    private fun validatePeriods(lumpSums: List<ProjectLumpSum>, project: Project) {
        val periodNumbers = project.periods.mapTo(HashSet()) { it.number }
        periodNumbers.add(0) // Preparation = period number 0
        if (lumpSums.any { !periodNumbers.contains(it.period) })
            throw I18nValidationException(i18nKey = "project.lumpSum.period.does.not.exist")
    }
}
