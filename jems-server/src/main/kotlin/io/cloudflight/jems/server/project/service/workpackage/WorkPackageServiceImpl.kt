package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectForm
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectWorkPackage
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.workpackage.WorkPackageRepository
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.toApplicantAndStatus
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel.EDIT
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel.MANAGE
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel.VIEW
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import org.springframework.data.domain.Sort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkPackageServiceImpl(
    private val workPackageRepository: WorkPackageRepository,
    private val projectRepository: ProjectRepository,
    private val projectCollaboratorRepository: UserProjectCollaboratorRepository,
    private val partnerCollaboratorRepository: UserPartnerCollaboratorRepository
) : WorkPackageService {

    companion object {
        private const val MAX_WORK_PACKAGES_PER_PROJECT = 20L
    }

    @Transactional(readOnly = true)
    override fun getProjectForWorkPackageId(id: Long): ProjectApplicantAndStatus =
        getWorkPackageOrThrow(id).project.let {
            val partnerCollaborators = partnerCollaboratorRepository.findAllByProjectId(id).map { collaborator -> collaborator.id.userId }
            val collaboratorsByLevel = projectCollaboratorRepository.findAllByIdProjectId(id)
                .groupBy { collaborator -> collaborator.level }
                .mapValues { entity -> entity.value.map { collaborator -> collaborator.id.userId }.toSet() }
            return it.toApplicantAndStatus(
                collaboratorViewIds = (collaboratorsByLevel[VIEW] ?: emptySet()) union partnerCollaborators,
                collaboratorEditIds = collaboratorsByLevel[EDIT] ?: emptySet(),
                collaboratorManageIds = collaboratorsByLevel[MANAGE] ?: emptySet(),
            )
        }

    @CanUpdateProjectForm
    @Transactional
    override fun createWorkPackage(projectId: Long, inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ResourceNotFoundException("project") }
        if (workPackageRepository.countAllByProjectId(projectId) >= MAX_WORK_PACKAGES_PER_PROJECT)
            throw I18nValidationException(i18nKey = "project.workPackage.max.allowed.reached")

        val workPackageCreated = workPackageRepository.save(inputWorkPackageCreate.toEntity(project))

        updateSortOnNumber(projectId)
        // entity is attached, number will have been updated
        return workPackageCreated.toOutputWorkPackage()
    }

    @PreAuthorize("@projectWorkPackageAuthorization.canUpdateProjectWorkPackage(#inputWorkPackageUpdate.id)")
    @Transactional
    override fun updateWorkPackage(projectId: Long, inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage {
        val oldWorkPackage = getWorkPackageOrThrow(inputWorkPackageUpdate.id)
        return workPackageRepository.save(oldWorkPackage.copy(inputWorkPackageUpdate.toTranslatedValues(oldWorkPackage)))
            .toOutputWorkPackage()
    }

    @CanUpdateProjectWorkPackage
    @Transactional
    override fun deleteWorkPackage(projectId: Long, workPackageId: Long) {
        workPackageRepository.deleteById(workPackageId)
        this.updateSortOnNumber(projectId)
    }

    private fun updateSortOnNumber(projectId: Long) {
        val sort = Sort.by(Sort.Direction.ASC, "id")

        workPackageRepository.findAllByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.number = index.plus(1) }
    }

    private fun getWorkPackageOrThrow(workPackageId: Long): WorkPackageEntity =
        workPackageRepository.findById(workPackageId)
            .orElseThrow { ResourceNotFoundException("workPackage") }

}
