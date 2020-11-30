package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.InputWorkPackageOutput
import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.OutputWorkPackageOutput
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorOutputRepository
import io.cloudflight.jems.server.project.repository.description.ProjectPeriodRepository
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageOutputPersistence
import io.cloudflight.jems.server.project.service.workpackage.toEntity
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackageOutput
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class WorkPackageOutputPersistenceProvider(
    private val workPackageOutputRepository: WorkPackageOutputRepository,
    private val workPackageRepository: WorkPackageRepository,
    private val indicatorOutputRepository: IndicatorOutputRepository,
    private val projectPeriodRepository: ProjectPeriodRepository
) : WorkPackageOutputPersistence {

    @Transactional
    override fun updateWorkPackageOutputs(
        projectId: Long,
        inputWorkPackageOutputs: Set<InputWorkPackageOutput>,
        workPackageId: Long
    ): Set<OutputWorkPackageOutput> {
        deleteWorkPackageOutputsOfCurrentWorkPackage(workPackageId)

        val workPackage = workPackageRepository.findById(workPackageId)
            .orElseThrow { ResourceNotFoundException("workpackage") }

        inputWorkPackageOutputs.forEach {
            val indicatorOutput =
                if (it.programmeOutputIndicatorId != null) indicatorOutputRepository.findById(it.programmeOutputIndicatorId!!)
                    .orElse(null)
                else null
            val projectPeriod =
                if (it.periodNumber != null)
                    projectPeriodRepository.findByIdProjectIdAndIdNumber(projectId, it.periodNumber!!)
                else null

            workPackageOutputRepository.save(it.toEntity(indicatorOutput = indicatorOutput, workPackage = workPackage, projectPeriod = projectPeriod))
        }

        return workPackageOutputRepository.findTop10ByWorkPackageIdOrderByOutputNumberAsc(workPackageId)
            .map { it.toOutputWorkPackageOutput() }
            .toSet()

    }

    @Transactional(readOnly = true)
    override fun getWorkPackageOutputsForWorkPackage(workPackageId: Long): Set<OutputWorkPackageOutput> {
        return workPackageOutputRepository.findTop10ByWorkPackageIdOrderByOutputNumberAsc(workPackageId)
            .map { it.toOutputWorkPackageOutput() }
            .toSet()
    }

    private fun deleteWorkPackageOutputsOfCurrentWorkPackage(workPackageId: Long) {
        workPackageOutputRepository.deleteAllByWorkPackageId(workPackageId)
    }

}
