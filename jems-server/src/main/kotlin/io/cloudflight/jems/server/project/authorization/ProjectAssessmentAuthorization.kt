package io.cloudflight.jems.server.project.authorization

import org.springframework.security.access.prepost.PreAuthorize

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentEligibilityEnter')")
annotation class CanSetEligibilityAssessment

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ProjectAssessmentQualityEnter')")
annotation class CanSetQualityAssessment
