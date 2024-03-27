package io.cloudflight.jems.server.payments.authorization

import org.springframework.security.access.prepost.PreAuthorize

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('PaymentsRetrieve')")
annotation class CanRetrievePayments

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('PaymentsUpdate')")
annotation class CanUpdatePayments

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('AdvancePaymentsRetrieve')")
annotation class CanRetrieveAdvancePayments

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('AdvancePaymentsUpdate')")
annotation class CanUpdateAdvancePayments

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('PaymentsToEcRetrieve')")
annotation class CanRetrievePaymentApplicationsToEc

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('PaymentsToEcUpdate')")
annotation class CanUpdatePaymentApplicationsToEc

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('PaymentsAuditRetrieve')")
annotation class CanRetrievePaymentsAudit

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('PaymentsAuditUpdate')")
annotation class CanUpdatePaymentsAudit

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('PaymentsAccountRetrieve')")
annotation class CanRetrievePaymentsAccount

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('PaymentsAccountUpdate')")
annotation class CanUpdatePaymentsAccount
