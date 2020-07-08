package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.APPLICANT_USER
import io.cloudflight.ems.security.PROGRAMME_USER
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.ProjectService
import org.springframework.stereotype.Component

@Component
class ProjectAuthorization(
    val securityService: SecurityService,
    val projectService: ProjectService
) {

    fun canReadProject(id: Long): Boolean {
        return (securityService.currentUser?.hasRole(ADMINISTRATOR)!!
                || securityService.currentUser?.hasRole(PROGRAMME_USER)!!
                || projectService.getById(id).applicant.id == securityService.currentUser?.user?.id)
    }

    fun canWriteProject(id: Long): Boolean {
        return securityService.currentUser?.hasRole(ADMINISTRATOR)!!
            || projectService.getById(id).applicant.id == securityService.currentUser?.user?.id
    }

    fun canCreateProject(): Boolean {
        return securityService.currentUser?.hasRole(ADMINISTRATOR)!!
            || securityService.currentUser?.hasRole(APPLICANT_USER)!!
    }

    fun canReadWriteProject(id: Long): Boolean {
        return canReadProject(id) && canWriteProject(id);
    }
}
