package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.CanRetrieveProject
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectWorkPackage
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectWorkPackage
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.workpackage.WorkPackageRepository
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.toApplicantAndStatus
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

    companion object {
        private const val MAX_WORK_PACKAGES_PER_PROJECT = 20L
    }

    @CanRetrieveProjectWorkPackage
    @Transactional(readOnly = true)
    override fun getWorkPackageById(workPackageId: Long): OutputWorkPackage =
        getWorkPackageOrThrow(workPackageId).toOutputWorkPackage()

    @Transactional(readOnly = true)
    override fun getProjectForWorkPackageId(id: Long): ProjectApplicantAndStatus =
        getWorkPackageOrThrow(id).project.toApplicantAndStatus()

    @CanRetrieveProject
    @Transactional(readOnly = true)
    override fun getWorkPackagesByProjectId(projectId: Long, pageable: Pageable): Page<OutputWorkPackageSimple> {
        return workPackageRepository.findAllByProjectId(projectId, pageable).map { it.toOutputWorkPackageSimple() }
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

    @PreAuthorize("hasAuthority('ProjectUpdate') || @projectWorkPackageAuthorization.canOwnerUpdateProjectWorkPackage(#inputWorkPackageUpdate.id)")
    @Transactional
    override fun updateWorkPackage(inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage {
        val oldWorkPackage = getWorkPackageOrThrow(inputWorkPackageUpdate.id)

        val toUpdate = oldWorkPackage.copy(
            translatedValues = inputWorkPackageUpdate.combineTranslatedValues(oldWorkPackage.id)
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
