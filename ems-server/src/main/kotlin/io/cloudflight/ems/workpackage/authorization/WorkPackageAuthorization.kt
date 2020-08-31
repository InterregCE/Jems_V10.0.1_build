package io.cloudflight.ems.workpackage.authorization

import io.cloudflight.ems.project.service.ProjectService
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.security.service.authorization.Authorization
import org.springframework.stereotype.Component

@Component
class WorkPackageAuthorization(
    override val securityService: SecurityService,
    private val projectService: ProjectService
) : Authorization(securityService) {

    fun canReadWorkPackages(): Boolean {
        return isAdmin()
    }

    fun canAccessWorkPackageDetails(projectId: Long): Boolean {
        val project = projectService.getById(projectId)

        return isAdmin() || isApplicantOwner(project)
    }

}
