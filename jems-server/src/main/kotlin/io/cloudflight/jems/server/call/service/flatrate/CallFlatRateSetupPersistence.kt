package io.cloudflight.jems.server.call.service.flatrate

import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate

interface CallFlatRateSetupPersistence {

    fun updateProjectCallFlatRate(callId: Long, flatRates: Set<ProjectCallFlatRate>)

    fun getProjectCallFlatRate(callId: Long): Set<ProjectCallFlatRate>

    fun getProjectCallFlatRateByPartnerId(partnerId: Long): Set<ProjectCallFlatRate>

}
