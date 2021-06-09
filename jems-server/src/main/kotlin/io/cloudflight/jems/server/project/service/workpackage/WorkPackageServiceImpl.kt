package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.workpackage.WorkPackageRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkPackageServiceImpl(
    private val workPackageRepository: WorkPackageRepository,
    private val projectRepository: ProjectRepository
) : WorkPackageService {

    companion object {
        private const val MAX_WORK_PACKAGES_PER_PROJECT = 20L
    }

    @CanUpdateProject
    @Transactional
    override fun createWorkPackage(projectId: Long, inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ResourceNotFoundException("project") }
        if (workPackageRepository.countAllByProjectId(projectId) >= MAX_WORK_PACKAGES_PER_PROJECT)
            throw I18nValidationException(i18nKey = "project.workPackage.max.allowed.reached")

        val workPackageCreated = workPackageRepository.save(inputWorkPackageCreate.toEntity(project))
        val workPackageSavedWithTranslations = workPackageRepository.save(
            workPackageCreated.copy(
                translatedValues = inputWorkPackageCreate.combineTranslatedValues(workPackageCreated.id))
        )
        updateSortOnNumber(projectId)
        // entity is attached, number will have been updated
        return workPackageSavedWithTranslations.toOutputWorkPackage()
    }

    @CanUpdateProject
    @Transactional
    override fun updateWorkPackage(projectId: Long, inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage {
        val oldWorkPackage = getWorkPackageOrThrow(inputWorkPackageUpdate.id)

        val toUpdate = oldWorkPackage.copy(
            translatedValues = inputWorkPackageUpdate.combineTranslatedValues(oldWorkPackage.id)
        )

        return workPackageRepository.save(toUpdate).toOutputWorkPackage()
    }

    @CanUpdateProject
    @Transactional
    override fun deleteWorkPackage(projectId: Long, workPackageId: Long) {
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
