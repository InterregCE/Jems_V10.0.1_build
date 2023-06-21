package io.cloudflight.jems.server.call.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.service.model.AllowedRealCosts
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallApplicationFormFieldsConfiguration
import io.cloudflight.jems.server.call.service.model.CallCostOption
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.call.service.model.PreSubmissionPlugins
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CallPersistence {

    fun getCalls(pageable: Pageable): Page<CallSummary>
    fun listCalls(status: CallStatus?): List<IdNamePair>
    fun getPublishedAndOpenCalls(pageable: Pageable): Page<CallSummary>
    fun getCallById(callId: Long): CallDetail
    fun getCallSummaryById(callId: Long): CallSummary
    fun getCallByProjectId(projectId: Long): CallDetail
    fun getCallSimpleByPartnerId(partnerId: Long): CallDetail
    fun getCallIdForNameIfExists(name: String): Long?
    fun createCall(call: Call, userId: Long): CallDetail
    fun updateCall(call: Call): CallDetail

    fun updateProjectCallFlatRate(callId: Long, flatRatesRequest: Set<ProjectCallFlatRate>): CallDetail
    fun existsAllProgrammeLumpSumsByIds(ids: Set<Long>): Boolean
    fun updateProjectCallLumpSum(callId: Long, lumpSumIds: Set<Long>): CallDetail
    fun existsAllProgrammeUnitCostsByIds(ids: Set<Long>): Boolean
    fun updateProjectCallUnitCost(callId: Long, unitCostIds: Set<Long>): CallDetail

    fun getAllowedRealCosts(callId: Long): AllowedRealCosts
    fun updateAllowedRealCosts(callId: Long, allowedRealCosts: AllowedRealCosts): AllowedRealCosts

    fun publishCall(callId: Long): CallSummary
    fun hasAnyCallPublished(): Boolean
    fun isCallPublished(callId: Long): Boolean

    fun getApplicationFormFieldConfigurations(callId: Long): CallApplicationFormFieldsConfiguration
    fun saveApplicationFormFieldConfigurations(
        callId: Long, applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration>
    ): CallDetail

    fun updateProjectCallStateAids(callId: Long, stateAids: Set<Long>): CallDetail

    fun updateProjectCallPreSubmissionCheckPlugin(callId: Long, pluginKeys: PreSubmissionPlugins) : CallDetail

    fun getCallCostOptionForProject(projectId: Long): CallCostOption
    fun getCallCostOption(callId: Long): CallCostOption
    fun updateCallCostOption(callId: Long, costOption: CallCostOption): CallCostOption
}

