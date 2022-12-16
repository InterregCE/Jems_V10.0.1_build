package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUsers

import io.cloudflight.jems.server.project.service.report.model.file.UserSimple

interface GetInstitutionUsersInteractor {
    fun getInstitutionUsers(partnerId: Long, institutionId: Long): List<UserSimple>
}
