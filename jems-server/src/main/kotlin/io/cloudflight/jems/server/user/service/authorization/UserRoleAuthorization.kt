package io.cloudflight.jems.server.user.service.authorization

import io.cloudflight.jems.server.authentication.model.ADMINISTRATOR
import org.springframework.security.access.prepost.PreAuthorize

@Retention(AnnotationRetention.RUNTIME)
//@PreAuthorize("hasAuthority('RoleRetrieve')")
@PreAuthorize("hasRole('$ADMINISTRATOR')")
annotation class CanRetrieveRole

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('RoleCreate')")
annotation class CanCreateRole

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('RoleUpdate')")
annotation class CanUpdateRole
