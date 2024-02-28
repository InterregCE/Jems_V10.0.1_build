package io.cloudflight.jems.server.call.service.get_call_checklists

import io.cloudflight.jems.server.call.service.model.CallChecklist
import org.springframework.data.domain.Sort

interface GetCallChecklistsInteractor {
    fun getCallChecklists(callId: Long, sort: Sort): List<CallChecklist>
}
