package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectUpdate') || @projectWorkPackageAuthorization.canOwnerUpdateProjectWorkPackage(#workPackageId)")
annotation class CanUpdateProjectWorkPackage

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectRetrieve') || @projectWorkPackageAuthorization.isUserOwnerOfWorkPackage(#projectId, #workPackageId, #version)")
annotation class CanRetrieveProjectWorkPackage

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectUpdate') || @projectWorkPackageAuthorization.canOwnerUpdateProjectInvestment(#investmentId)")
annotation class CanUpdateProjectWorkPackageInvestment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectRetrieve') || @projectWorkPackageAuthorization.isUserOwnerOfInvestment(#projectId, #investmentId, #version)")
annotation class CanRetrieveProjectWorkPackageInvestment

@Component
class ProjectWorkPackageAuthorization(
    override val securityService: SecurityService,
    val workPackageService: WorkPackageService,
    val workPackagePersistence: WorkPackagePersistence,
    val projectPersistence: ProjectPersistence
) : Authorization(securityService) {

    fun canOwnerUpdateProjectWorkPackage(workPackageId: Long): Boolean {
        val project = getProjectFromWorkPackageId(workPackageId)
        return project.projectStatus.hasNotBeenSubmittedYet() && isActiveUserIdEqualTo(project.applicantId)
    }

    fun isUserOwnerOfWorkPackage(projectId: Long, workPackageId: Long, version: String?): Boolean {
        val project = if (!version.isNullOrBlank()) projectPersistence.getApplicantAndStatusById(projectId) else getProjectFromWorkPackageId(workPackageId)
        return isActiveUserIdEqualTo(userId = project.applicantId)
    }


    private fun getProjectFromWorkPackageId(workPackageId: Long): ProjectApplicantAndStatus =
        workPackageService.getProjectForWorkPackageId(workPackageId)

    fun canOwnerUpdateProjectInvestment(investmentId: Long): Boolean {
        val project = getProjectFromInvestmentId(investmentId)
        return project.projectStatus.hasNotBeenSubmittedYet() && isActiveUserIdEqualTo(project.applicantId)
    }

    fun isUserOwnerOfInvestment(projectId: Long, investmentId: Long, version: String?): Boolean {
        val project = if (!version.isNullOrBlank()) projectPersistence.getApplicantAndStatusById(projectId) else getProjectFromInvestmentId(investmentId)
        return isActiveUserIdEqualTo(project.applicantId)
    }

    private fun getProjectFromInvestmentId(investmentId: Long): ProjectApplicantAndStatus =
        workPackagePersistence.getProjectFromWorkPackageInvestment(investmentId)

}
