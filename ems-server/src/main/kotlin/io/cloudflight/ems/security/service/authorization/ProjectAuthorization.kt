package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.PROGRAMME_USER
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.ProjectService
import org.springframework.stereotype.Component

@Component
class ProjectAuthorization(
    val securityService: SecurityService,
    val projectService: ProjectService
) {

    fun canAccessProject(id: Long): Boolean {
        return securityService.currentUser?.hasRole(ADMINISTRATOR)!!
                || securityService.currentUser?.hasRole(PROGRAMME_USER)!!
                || projectService.getProjectById(id).applicant.id == securityService.currentUser?.user?.id
    }
}
