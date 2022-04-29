package io.cloudflight.jems.server.project.authorization

import org.springframework.security.access.prepost.PreAuthorize

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@authorization.hasPermission('ProjectAssessmentChecklistUpdate', #createCheckList.relatedToId)")
annotation class CanCreateChecklistAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentChecklistUpdate')")
annotation class CanUpdateChecklistAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentChecklistUpdate')")
annotation class CanDeleteChecklistAssessment
