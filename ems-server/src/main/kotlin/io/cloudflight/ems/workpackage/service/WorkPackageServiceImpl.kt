package io.cloudflight.ems.workpackage.service

import io.cloudflight.ems.api.workpackage.dto.InputWorkPackageCreate
import io.cloudflight.ems.api.workpackage.dto.InputWorkPackageUpdate
import io.cloudflight.ems.api.workpackage.dto.OutputWorkPackage
import io.cloudflight.ems.api.workpackage.dto.OutputWorkPackageSimple
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.project.repository.ProjectRepository
import io.cloudflight.ems.workpackage.repository.WorkPackageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkPackageServiceImpl(
    private val workPackageRepository: WorkPackageRepository,
    private val projectRepository: ProjectRepository
) : WorkPackageService {

    @Transactional(readOnly = true)
    override fun getWorkPackageById(id: Long): OutputWorkPackage {
        return workPackageRepository.findById(id).map { it.toOutputWorkPackage() }
            .orElseThrow { ResourceNotFoundException("workpackage") }
    }

    @Transactional(readOnly = true)
    override fun getWorkPackagesByProjectId(projectId: Long, pageable: Pageable): Page<OutputWorkPackageSimple> {
        return workPackageRepository.findAllByProjectId(projectId, pageable).map { it.toOutputWorkPackageSimple() }
    }

    @Transactional
    override fun createWorkPackage(projectId: Long, inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ResourceNotFoundException("project") }

        val generatedWorkPackageNumber = getWorkPackageNumber(projectId)

        return workPackageRepository.save(
            inputWorkPackageCreate.toEntity(project, generatedWorkPackageNumber)
        ).toOutputWorkPackage()
    }

    @Transactional
    override fun updateWorkPackage(projectId: Long, inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage {
        val oldWorkPackage = workPackageRepository.findFirstByProjectIdAndId(projectId, inputWorkPackageUpdate.id)

        val toUpdate = oldWorkPackage.copy(
            name = inputWorkPackageUpdate.name,
            specificObjective = inputWorkPackageUpdate.specificObjective,
            objectiveAndAudience = inputWorkPackageUpdate.objectiveAndAudience
        )

        return workPackageRepository.save(toUpdate).toOutputWorkPackage()
    }

    private fun getWorkPackageNumber(projectId: Long): Int {
        val previousWorkPackageNumber: Int? =
            workPackageRepository.findFirstByProjectIdOrderByNumberDesc(projectId)?.number

        return if (previousWorkPackageNumber != null) {
            previousWorkPackageNumber + 1
        } else 1
    }

}
