package io.cloudflight.jems.server.project.service.lumpsum.update_project_lump_sums

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectLumpSums(
    private val persistence: ProjectLumpSumPersistence,
    private val projectPersistence: ProjectPersistence,
) : UpdateProjectLumpSumsInteractor {

    companion object {
        private const val MAX_ALLOWED_PROJECT_LUMP_SUMS = 50

        // predefined periods
        private const val PREPARATION_PERIOD = 0
        private const val CLOSURE_PERIOD = 255
    }

    @Transactional
    @CanUpdateProject
    override fun updateLumpSums(projectId: Long, lumpSums: List<ProjectLumpSum>): List<ProjectLumpSum> {
        validateMaxAllowedSize(lumpSums)
        validateWrongSplitting(lumpSums, projectPersistence.getProjectCallSettings(projectId))
        validatePeriods(lumpSums, projectPersistence.getProjectPeriods(projectId))

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
        val lumpSumsById = settings.lumpSums.associateBy { it.id }

        if (lumpSums.any {
                it.lumpSumContributions.size > 1 && lumpSumsById[it.programmeLumpSumId].isLumpSumNotSplittable()
            })
            throw I18nValidationException(i18nKey = "project.lumpSum.splitting.not.allowed")
    }

    private fun validatePeriods(lumpSums: List<ProjectLumpSum>, projectPeriods: List<ProjectPeriod>) {
        val periodNumbers = projectPeriods.mapTo(HashSet()) { it.number }
        periodNumbers.add(PREPARATION_PERIOD)
        periodNumbers.add(CLOSURE_PERIOD)
        if (lumpSums.mapNotNullTo(HashSet()) { it.period }.any { !periodNumbers.contains(it) })
            throw I18nValidationException(i18nKey = "project.lumpSum.period.does.not.exist")
    }
}
