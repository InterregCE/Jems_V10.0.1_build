package io.cloudflight.jems.server.controllerInstitution.authorization

import org.springframework.security.access.prepost.PreAuthorize


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('InstitutionsAssignmentRetrieve')")
annotation class CanViewInstitutionAssignments


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('InstitutionsAssignmentUpdate')")
annotation class CanAssignInstitutionToPartner

