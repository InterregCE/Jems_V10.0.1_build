package io.cloudflight.jems.server.call.service.list_calls

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.service.model.IdNamePair

interface ListCallsInteractor {

    fun list(status: CallStatus?): List<IdNamePair>

}
