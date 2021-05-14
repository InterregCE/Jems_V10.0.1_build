package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectUpdate') || @projectWorkPackageAuthorization.canOwnerUpdateProjectWorkPackage(#workPackageId)")
annotation class CanUpdateProjectWorkPackage

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectRetrieve') || @projectWorkPackageAuthorization.isUserOwnerOfWorkPackage(#workPackageId)")
annotation class CanRetrieveProjectWorkPackage

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectUpdate') || @projectWorkPackageAuthorization.canOwnerUpdateProjectInvestment(#investmentId)")
annotation class CanUpdateProjectWorkPackageInvestment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectRetrieve') || @projectWorkPackageAuthorization.isUserOwnerOfInvestment(#investmentId)")
annotation class CanRetrieveProjectWorkPackageInvestment

@Component
class ProjectWorkPackageAuthorization(
    override val securityService: SecurityService,
    val workPackageService: WorkPackageService,
    val workPackagePersistence: WorkPackagePersistence,
) : Authorization(securityService) {

    fun canOwnerUpdateProjectWorkPackage(workPackageId: Long): Boolean {
        val project = getProjectFromWorkPackageId(workPackageId)
        return project.projectStatus.isNotSubmittedNow() && isActiveUserIdEqualTo(project.applicantId)
    }

    fun isUserOwnerOfWorkPackage(workPackageId: Long): Boolean =
        isActiveUserIdEqualTo(userId = getProjectFromWorkPackageId(workPackageId).applicantId)

    private fun getProjectFromWorkPackageId(workPackageId: Long): ProjectApplicantAndStatus =
        workPackageService.getProjectForWorkPackageId(workPackageId)

    fun canOwnerUpdateProjectInvestment(investmentId: Long): Boolean {
        val project = getProjectFromInvestmentId(investmentId)
        return project.projectStatus.isNotSubmittedNow() && isActiveUserIdEqualTo(project.applicantId)
    }

    fun isUserOwnerOfInvestment(investmentId: Long): Boolean =
        isActiveUserIdEqualTo(userId = getProjectFromInvestmentId(investmentId).applicantId)

    private fun getProjectFromInvestmentId(investmentId: Long): ProjectApplicantAndStatus =
        workPackagePersistence.getProjectFromWorkPackageInvestment(investmentId)

}
