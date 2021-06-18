package io.cloudflight.jems.server.call.service

import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfigurationSummary
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CallPersistence {

    fun getCalls(pageable: Pageable): Page<CallSummary>
    fun getPublishedAndOpenCalls(pageable: Pageable): Page<CallSummary>
    fun getCallById(callId: Long): CallDetail
    fun getCallIdForNameIfExists(name: String): Long?
    fun createCall(call: Call, userId: Long): CallDetail
    fun updateCall(call: Call): CallDetail

    fun updateProjectCallFlatRate(callId: Long, flatRates: Set<ProjectCallFlatRate>): CallDetail
    fun existsAllProgrammeLumpSumsByIds(ids: Set<Long>): Boolean
    fun updateProjectCallLumpSum(callId: Long, lumpSumIds: Set<Long>): CallDetail
    fun existsAllProgrammeUnitCostsByIds(ids: Set<Long>): Boolean
    fun updateProjectCallUnitCost(callId: Long, unitCostIds: Set<Long>): CallDetail

    fun publishCall(callId: Long): CallSummary
    fun hasAnyCallPublished(): Boolean

    fun getApplicationFormConfiguration(id: Long): ApplicationFormConfiguration
    fun listApplicationFormConfigurations(): List<ApplicationFormConfigurationSummary>
    fun updateApplicationFormConfigurations(applicationFormConfiguration: ApplicationFormConfiguration)
}
