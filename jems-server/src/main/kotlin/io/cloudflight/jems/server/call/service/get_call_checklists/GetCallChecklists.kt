package io.cloudflight.jems.server.call.service.get_call_checklists

import io.cloudflight.jems.server.call.authorization.CanRetrieveCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallChecklist
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCallChecklists(
    private val persistence: CallPersistence
) : GetCallChecklistsInteractor {

    @CanRetrieveCall
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallChecklistsException::class)
    override fun getCallChecklists(callId: Long, sort: Sort): List<CallChecklist> {
        return persistence.getCallChecklists(callId, sort)
    }
}
