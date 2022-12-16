package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUsers

import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerUser

interface GetInstitutionUsersInteractor {
    fun getInstitutionUsers(partnerId: Long, institutionId: Long): List<ControllerUser>
}
