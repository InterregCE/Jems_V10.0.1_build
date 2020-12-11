package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment.GetWorkPackageInvestmentInteractor
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import java.util.*

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canUpdateProjectWorkPackage(#workPackageId)")
annotation class CanUpdateProjectWorkPackage

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canReadProjectWorkPackage(#workPackageId)")
annotation class CanReadProjectWorkPackage

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canUpdateProjectWorkPackageInvestment(#investmentId)")
annotation class CanUpdateProjectWorkPackageInvestment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectWorkPackageAuthorization.canReadProjectWorkPackageInvestment(#investmentId)")
annotation class CanReadProjectWorkPackageInvestment

@Component
class ProjectWorkPackageAuthorization(
    override val securityService: SecurityService,
    val workPackageService: WorkPackageService,
    val workPackagePersistence: WorkPackagePersistence,
    val projectAuthorization: ProjectAuthorization
) : Authorization(securityService) {

    fun canReadProjectWorkPackage(workPackageId: Long): Boolean =
        projectAuthorization.canReadProject(workPackageService.getProjectIdForWorkPackageId(workPackageId))

    fun canUpdateProjectWorkPackage(workPackageId: Long): Boolean =
        projectAuthorization.canUpdateProject(workPackageService.getProjectIdForWorkPackageId(workPackageId))

    fun canReadProjectWorkPackageInvestment(investmentId: Long): Boolean =
        projectAuthorization.canReadProject(
            workPackagePersistence.getProjectIdFromWorkPackageInvestment(investmentId)
        )

    fun canUpdateProjectWorkPackageInvestment(investmentId: Long): Boolean =
        projectAuthorization.canUpdateProject(
            workPackagePersistence.getProjectIdFromWorkPackageInvestment(investmentId)
        )

}
