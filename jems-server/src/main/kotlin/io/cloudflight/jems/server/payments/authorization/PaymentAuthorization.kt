package io.cloudflight.jems.server.payments.authorization

import org.springframework.security.access.prepost.PreAuthorize

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('PaymentsRetrieve')")
annotation class CanRetrievePayments

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('PaymentsUpdate')")
annotation class CanUpdatePayments
