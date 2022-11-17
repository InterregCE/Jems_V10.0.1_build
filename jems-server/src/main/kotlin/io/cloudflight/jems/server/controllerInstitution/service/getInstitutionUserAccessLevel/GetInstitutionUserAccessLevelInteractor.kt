package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUserAccessLevel

import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel

interface GetInstitutionUserAccessLevelInteractor {

    fun getControllerUserAccessLevelForPartner(partnerId: Long): UserInstitutionAccessLevel?
}
