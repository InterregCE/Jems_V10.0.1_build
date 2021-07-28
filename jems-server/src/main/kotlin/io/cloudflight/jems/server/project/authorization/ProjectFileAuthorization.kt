package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectFileAssessmentRetrieve')")
annotation class CanRetrieveProjectAssessmentFile

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectFileAssessmentRetrieve')")
annotation class CanRetrieveProjectAssessmentFiles

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectFileAssessmentUpdate')")
annotation class CanUpdateProjectAssessmentFile

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectFileApplicationRetrieve') || @projectAuthorization.isUserOwnerOfProject(#projectId)")
annotation class CanRetrieveProjectApplicationFile

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectFileApplicationRetrieve') || @projectAuthorization.isUserOwnerOfProject(#projectId)")
annotation class CanRetrieveProjectApplicationFiles

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectFileApplicationUpdate') || @projectAuthorization.canUpdateProject(#projectId)")
annotation class CanUpdateProjectApplicationFile

@Component
class ProjectFileAuthorization(
    override val securityService: SecurityService,
): Authorization(securityService)
