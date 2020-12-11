package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorOutputRepository
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackage
import io.cloudflight.jems.server.project.repository.description.ProjectPeriodRepository
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageOutputPersistence
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class WorkPackageOutputPersistenceProvider(
    private val workPackageRepository: WorkPackageRepository,
    private val indicatorOutputRepository: IndicatorOutputRepository,
    private val projectPeriodRepository: ProjectPeriodRepository
) : WorkPackageOutputPersistence {

    @Transactional
    override fun updateWorkPackageOutputs(
        projectId: Long,
        workPackageOutputs: Set<WorkPackageOutputUpdate>,
        workPackageId: Long
    ): Set<WorkPackageOutput> {
        val workPackage = getWorkPackageOrThrow(workPackageId)

        workPackage.workPackageOutputs.clear()
        workPackageOutputs.forEach {
            val indicatorOutput =
                if (it.programmeOutputIndicatorId != null) indicatorOutputRepository.findById(it.programmeOutputIndicatorId)
                    .orElse(null)
                else null
            val projectPeriod =
                if (it.periodNumber != null)
                    projectPeriodRepository.findByIdProjectIdAndIdNumber(projectId, it.periodNumber)
                else null

            workPackage.workPackageOutputs.add(it.toEntity(indicatorOutput, workPackage, projectPeriod))
        }

        return workPackage.workPackageOutputs.toWorkPackageOutputSet()

    }

    @Transactional(readOnly = true)
    override fun getWorkPackageOutputsForWorkPackage(workPackageId: Long): Set<WorkPackageOutput> =
        getWorkPackageOrThrow(workPackageId).workPackageOutputs
            .toWorkPackageOutputSet()

    private fun getWorkPackageOrThrow(workPackageId: Long): WorkPackage =
        workPackageRepository.findById(workPackageId).orElseThrow { ResourceNotFoundException("workpackage") }

}
