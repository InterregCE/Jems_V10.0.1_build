package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.authorization.CanReadProjectWorkPackage
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectWorkPackage
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.workpackage.WorkPackageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkPackageServiceImpl(
    private val workPackageRepository: WorkPackageRepository,
    private val projectRepository: ProjectRepository
) : WorkPackageService {

    @CanReadProjectWorkPackage
    @Transactional(readOnly = true)
    override fun getWorkPackageById(workPackageId: Long): OutputWorkPackage =
        getWorkPackageOrThrow(workPackageId).toOutputWorkPackage()

    @Transactional(readOnly = true)
    override fun getProjectIdForWorkPackageId(id: Long): Long =
        getWorkPackageOrThrow(id).project.id

    @CanReadProject
    @Transactional(readOnly = true)
    override fun getWorkPackagesByProjectId(projectId: Long, pageable: Pageable): Page<OutputWorkPackageSimple> {
        return workPackageRepository.findAllByProjectId(projectId, pageable).map { it.toOutputWorkPackageSimple() }
    }

    @CanUpdateProject
    @Transactional
    override fun createWorkPackage(projectId: Long, inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ResourceNotFoundException("project") }

        val workPackageCreated = workPackageRepository.save(inputWorkPackageCreate.toEntity(project))
        updateSortOnNumber(projectId)
        // entity is attached, number will have been updated
        return workPackageCreated.toOutputWorkPackage()
    }

    @PreAuthorize("@projectWorkPackageAuthorization.canUpdateProjectWorkPackage(#inputWorkPackageUpdate.id)")
    @Transactional
    override fun updateWorkPackage(inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage {
        val oldWorkPackage = getWorkPackageOrThrow(inputWorkPackageUpdate.id)

        val toUpdate = oldWorkPackage.copy(
            name = inputWorkPackageUpdate.name,
            specificObjective = inputWorkPackageUpdate.specificObjective,
            objectiveAndAudience = inputWorkPackageUpdate.objectiveAndAudience
        )

        return workPackageRepository.save(toUpdate).toOutputWorkPackage()
    }

    @CanUpdateProjectWorkPackage
    @Transactional
    override fun deleteWorkPackage(workPackageId: Long) {
        val projectId = getWorkPackageOrThrow(workPackageId).project.id
        workPackageRepository.deleteById(workPackageId)
        this.updateSortOnNumber(projectId)
    }

    private fun updateSortOnNumber(projectId: Long) {
        val sort = Sort.by(Sort.Direction.ASC, "id")

        val projectWorkPackages = workPackageRepository.findAllByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.copy(number = index.plus(1)) }
        workPackageRepository.saveAll(projectWorkPackages)
    }

    private fun getWorkPackageOrThrow(workPackageId: Long): WorkPackageEntity =
        workPackageRepository.findById(workPackageId)
            .orElseThrow { ResourceNotFoundException("workPackage") }

}
