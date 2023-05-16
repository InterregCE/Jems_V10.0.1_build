package io.cloudflight.jems.server.controllerInstitution.service.getControllerInstitutionNUTS

import io.cloudflight.jems.api.nuts.dto.OutputNuts

interface GetControllerInstitutionNUTSInteractor {

    fun getAvailableRegionsForCurrentUser(): List<OutputNuts>
}
