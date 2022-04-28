package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectChecklistAuthorization.hasPermissionOrIsEditCollaborator('ProjectAssessmentChecklistUpdate', #createCheckList)")
annotation class CanCreateChecklistAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentChecklistUpdate')")
annotation class CanUpdateChecklistAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentChecklistUpdate')")
annotation class CanDeleteChecklistAssessment

@Component
class ProjectChecklistAuthorization(
    override val securityService: SecurityService,
    val userProjectPersistenceProvider: UserProjectPersistenceProvider,
    val projectPersistence: ProjectPersistence
) : Authorization(securityService) {

    fun hasPermissionOrIsEditCollaborator(
        permission: UserRolePermission,
        createCheckList: CreateChecklistInstanceModel
    ): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(createCheckList.relatedToId)

        val userIdsForProject = userProjectPersistenceProvider.getUserIdsForProject(createCheckList.relatedToId)
        val userIdsForProjectWithEditLevel = project.getUserIdsWithEditLevel()

        val hasProjectPermissions =
            isActiveUserIdEqualToOneOf(userIdsForProject union userIdsForProjectWithEditLevel)

        if (hasProjectPermissions && hasNonProjectAuthority(permission))
            return true
        else
            throw ResourceNotFoundException("project")
    }

}
