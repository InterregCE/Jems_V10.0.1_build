package io.cloudflight.jems.server.project.authorization

import org.springframework.security.access.prepost.PreAuthorize

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentEligibilityUpdate')")
annotation class CanSetEligibilityAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentQualityUpdate')")
annotation class CanSetQualityAssessment
