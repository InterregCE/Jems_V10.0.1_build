package io.cloudflight.jems.server.notification.inApp.authorization

import org.springframework.security.access.prepost.PreAuthorize

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('NotificationsRetrieve')")
annotation class CanRetrieveNotifications
