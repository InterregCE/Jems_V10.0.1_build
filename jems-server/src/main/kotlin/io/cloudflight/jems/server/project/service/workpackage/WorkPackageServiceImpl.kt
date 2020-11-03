package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.WorkPackageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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

        val workPackageCreated = workPackageRepository.save(inputWorkPackageCreate.toEntity(project))
        updateSortOnNumber(projectId)
        // entity is attached, number will have been updated
        return workPackageCreated.toOutputWorkPackage()
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

    private fun updateSortOnNumber(projectId: Long) {
        val sort = Sort.by(Sort.Direction.ASC, "id")

        val projectWorkPackages = workPackageRepository.findAllByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.copy(number = index.plus(1)) }
        workPackageRepository.saveAll(projectWorkPackages)
    }

    @Transactional
    override fun deleteWorkPackage(projectId: Long, id: Long) {
        this.workPackageRepository.deleteById(id)
        this.updateSortOnNumber(projectId)
    }

}
