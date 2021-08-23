package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.CallApi
import io.cloudflight.jems.api.call.dto.AllowedRealCostsDTO
import io.cloudflight.jems.api.call.dto.CallDTO
import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.call.dto.CallUpdateRequestDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.server.call.service.create_call.CreateCallInteractor
import io.cloudflight.jems.server.call.service.get_allow_real_costs.GetAllowedRealCostsInteractor
import io.cloudflight.jems.server.call.service.get_call.GetCallInteractor
import io.cloudflight.jems.server.call.service.list_calls.ListCallsInteractor
import io.cloudflight.jems.server.call.service.publish_call.PublishCallInteractor
import io.cloudflight.jems.server.call.service.update_allow_real_costs.UpdateAllowedRealCostsInteractor
import io.cloudflight.jems.server.call.service.update_call.UpdateCallInteractor
import io.cloudflight.jems.server.call.service.update_call_flat_rates.UpdateCallFlatRatesInteractor
import io.cloudflight.jems.server.call.service.update_call_lump_sums.UpdateCallLumpSumsInteractor
import io.cloudflight.jems.server.call.service.update_call_unit_costs.UpdateCallUnitCostsInteractor
import io.cloudflight.jems.server.common.toDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class CallController(
    private val getCall: GetCallInteractor,
    private val listCalls: ListCallsInteractor,
    private val createCall: CreateCallInteractor,
    private val updateCall: UpdateCallInteractor,
    private val publishCall: PublishCallInteractor,
    private val updateCallFlatRates: UpdateCallFlatRatesInteractor,
    private val updateAllowedRealCosts: UpdateAllowedRealCostsInteractor,
    private val getAllowedRealCosts: GetAllowedRealCostsInteractor,
    private val updateCallLumpSums: UpdateCallLumpSumsInteractor,
    private val updateCallUnitCosts: UpdateCallUnitCostsInteractor,
) : CallApi {

    override fun getCalls(pageable: Pageable): Page<CallDTO> =
        getCall.getCalls(pageable).toDto()

    override fun listCalls(): List<IdNamePairDTO> =
        listCalls.list().toDTO()

    override fun getPublishedCalls(pageable: Pageable): Page<CallDTO> =
        getCall.getPublishedCalls(pageable).toDto()

    override fun getCallById(callId: Long): CallDetailDTO =
        getCall.getCallById(callId = callId).toDto()

    override fun createCall(call: CallUpdateRequestDTO): CallDetailDTO =
        createCall.createCallInDraft(call = call.toModel()).toDto()

    override fun updateCall(call: CallUpdateRequestDTO): CallDetailDTO =
        updateCall.updateCall(call = call.toModel()).toDto()

    override fun publishCall(callId: Long) =
        publishCall.publishCall(callId = callId).toDto()

    override fun updateCallFlatRateSetup(callId: Long, flatRateSetup: FlatRateSetupDTO) =
        updateCallFlatRates.updateFlatRateSetup(callId, flatRateSetup.toModel()).toDto()

    override fun getAllowedRealCosts(callId: Long): AllowedRealCostsDTO =
        getAllowedRealCosts.getAllowedRealCosts(callId).toDto()

    override fun updateAllowedRealCosts(callId: Long, allowedRealCosts: AllowedRealCostsDTO): AllowedRealCostsDTO =
        updateAllowedRealCosts.updateAllowedRealCosts(callId, allowedRealCosts.toModel()).toDto()

    override fun updateCallLumpSums(callId: Long, lumpSumIds: Set<Long>) =
        updateCallLumpSums.updateLumpSums(callId, lumpSumIds).toDto()

    override fun updateCallUnitCosts(callId: Long, unitCostIds: Set<Long>) =
        updateCallUnitCosts.updateUnitCosts(callId, unitCostIds).toDto()

}
