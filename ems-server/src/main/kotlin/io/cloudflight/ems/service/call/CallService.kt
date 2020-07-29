package io.cloudflight.ems.service.call

import io.cloudflight.ems.api.dto.call.InputCallCreate
import io.cloudflight.ems.api.dto.call.InputCallUpdate
import io.cloudflight.ems.api.dto.call.OutputCall
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CallService {

    fun createCall(inputCall: InputCallCreate): OutputCall
    fun updateCall(inputCall: InputCallUpdate): OutputCall
    fun getCallById(id: Long): OutputCall
    fun getCalls(pageable: Pageable): Page<OutputCall>

}
