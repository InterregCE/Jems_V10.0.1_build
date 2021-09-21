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
@PreAuthorize("hasAuthority('ProjectFormRetrieve') || @projectWorkPackageAuthorization.isUserOwnerOfWorkPackage(#projectId, #workPackageId, #version)")
annotation class CanRetrieveProjectWorkPackage

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canUpdateProjectInvestment(#workPackageInvestment.id)")
annotation class CanUpdateProjectWorkPackageInvestment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canUpdateProjectInvestment(#investmentId)")
annotation class CanDeleteProjectWorkPackageInvestment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectFormRetrieve') || @projectWorkPackageAuthorization.isUserOwnerOfInvestment(#projectId, #investmentId, #version)")
annotation class CanRetrieveProjectWorkPackageInvestment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectFormRetrieve') || hasAuthority('ProjectFileApplicationRetrieve') || @projectAuthorization.isUserOwnerOfProject(#projectId)")
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
        val canSeeWorkPackage = hasPermission(UserRolePermission.ProjectFormUpdate) || isActiveUserIdEqualTo(project.applicantId)
        if (canSeeWorkPackage)
            return project.projectStatus.hasNotBeenSubmittedYet()
        throw ResourceNotFoundException("project")
    }

    fun isUserOwnerOfWorkPackage(projectId: Long, workPackageId: Long, version: String?): Boolean {
        val project = if (!version.isNullOrBlank()) projectPersistence.getApplicantAndStatusById(projectId) else getProjectFromWorkPackageId(workPackageId)
        return isActiveUserIdEqualTo(userId = project.applicantId)
    }


    private fun getProjectFromWorkPackageId(workPackageId: Long): ProjectApplicantAndStatus =
        workPackageService.getProjectForWorkPackageId(workPackageId)

    fun canUpdateProjectInvestment(investmentId: Long): Boolean {
        val project = getProjectFromInvestmentId(investmentId)
        val canSeeInvestment = hasPermission(UserRolePermission.ProjectFormUpdate) || isActiveUserIdEqualTo(project.applicantId)
        if (canSeeInvestment)
            return project.projectStatus.hasNotBeenSubmittedYet()
        throw ResourceNotFoundException("project")
    }

    fun isUserOwnerOfInvestment(projectId: Long, investmentId: Long, version: String?): Boolean {
        val project = if (!version.isNullOrBlank()) projectPersistence.getApplicantAndStatusById(projectId) else getProjectFromInvestmentId(investmentId)
        return isActiveUserIdEqualTo(project.applicantId)
    }

    private fun getProjectFromInvestmentId(investmentId: Long): ProjectApplicantAndStatus =
        workPackagePersistence.getProjectFromWorkPackageInvestment(investmentId)

}
