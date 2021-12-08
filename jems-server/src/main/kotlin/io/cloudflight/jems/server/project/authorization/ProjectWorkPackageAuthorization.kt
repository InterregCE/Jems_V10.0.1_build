package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canUpdateProjectWorkPackage(#workPackageId)")
annotation class CanUpdateProjectWorkPackage

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canRetrieveProjectWorkPackage(#projectId, #workPackageId, #version)")
annotation class CanRetrieveProjectWorkPackage

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canUpdateProjectWorkPackageInvestment(#workPackageInvestment.id)")
annotation class CanUpdateProjectWorkPackageInvestment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canUpdateProjectWorkPackageInvestment(#investmentId)")
annotation class CanDeleteProjectWorkPackageInvestment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canRetrieveProjectWorkPackageInvestment(#projectId, #investmentId, #version)")
annotation class CanRetrieveProjectWorkPackageInvestment

@Retention(AnnotationRetention.RUNTIME)
// ProjectFileApplicationRetrieve is temporary hack because of broken File Upload screen,
// where people needs to see partners even when they cannot see project
@PreAuthorize("@projectAuthorization.hasPermission('ProjectFormRetrieve', #projectId) || @projectAuthorization.hasPermission('ProjectFileApplicationRetrieve', #projectId) || @projectAuthorization.userOwnerOrViewCollaboratorOrThrow(#projectId)")
annotation class CanRetrieveProjectWorkPackageInvestmentSummaries

@Component
class ProjectWorkPackageAuthorization(
    override val securityService: SecurityService,
    val workPackageService: WorkPackageService,
    val workPackagePersistence: WorkPackagePersistence,
    val projectPersistence: ProjectPersistence
) : Authorization(securityService) {

    fun canUpdateProjectWorkPackage(workPackageId: Long): Boolean {
        val project = getProjectFromWorkPackageId(workPackageId)
        val canSeeWorkPackage = hasPermissionForProject(UserRolePermission.ProjectFormUpdate, projectId = project.projectId)
            || isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())
        if (canSeeWorkPackage)
            return project.projectStatus.canBeModified()
        throw ResourceNotFoundException("project")
    }

    fun canRetrieveProjectWorkPackage(projectId: Long, workPackageId: Long, version: String? = null): Boolean {
        val project = getProject(projectId, workPackageId, version)
        return hasPermissionForProject(UserRolePermission.ProjectFormRetrieve, projectId = project.projectId)
            || isActiveUserIdEqualToOneOf(project.getUserIdsWithViewLevel())
    }

    fun canRetrieveProjectWorkPackageInvestment(projectId: Long, investmentId: Long, version: String? = null): Boolean {
        val project = workPackagePersistence.getProjectFromWorkPackageInvestment(investmentId)
        return hasPermissionForProject(UserRolePermission.ProjectFormRetrieve, projectId = project.projectId)
            || isActiveUserIdEqualToOneOf(project.getUserIdsWithViewLevel())
    }

    private fun getProject(projectId: Long, workPackageId: Long, version: String?): ProjectApplicantAndStatus =
        // TODO this is still security vulnerability, because work package might be from different project
        if (!version.isNullOrBlank()) projectPersistence.getApplicantAndStatusById(projectId) else getProjectFromWorkPackageId(workPackageId)

    private fun getProjectFromWorkPackageId(workPackageId: Long): ProjectApplicantAndStatus =
        workPackageService.getProjectForWorkPackageId(workPackageId)

    fun canUpdateProjectWorkPackageInvestment(investmentId: Long): Boolean {
        val project = getProjectFromInvestmentId(investmentId)
        val canSeeInvestment = hasPermissionForProject(UserRolePermission.ProjectFormUpdate, projectId = project.projectId)
            || isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())
        if (canSeeInvestment)
            return project.projectStatus.canBeModified()
        throw ResourceNotFoundException("project")
    }

    private fun getProjectFromInvestmentId(investmentId: Long): ProjectApplicantAndStatus =
        workPackagePersistence.getProjectFromWorkPackageInvestment(investmentId)

}
