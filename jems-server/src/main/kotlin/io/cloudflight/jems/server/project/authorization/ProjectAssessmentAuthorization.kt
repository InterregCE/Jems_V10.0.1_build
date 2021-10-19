package io.cloudflight.jems.server.project.authorization

import org.springframework.security.access.prepost.PreAuthorize

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectAssessmentEligibilityEnter', #projectId)")
annotation class CanSetEligibilityAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectAssessmentEligibilityEnter', #projectId)")
annotation class CanSetQualityAssessment
