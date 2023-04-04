package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUsers

import io.cloudflight.jems.server.common.file.service.model.UserSimple

interface GetInstitutionUsersInteractor {
    fun getInstitutionUsers(partnerId: Long, institutionId: Long): List<UserSimple>
}
