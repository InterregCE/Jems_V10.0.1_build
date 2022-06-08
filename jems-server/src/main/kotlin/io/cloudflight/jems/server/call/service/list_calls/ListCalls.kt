package io.cloudflight.jems.server.call.service.list_calls

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.authorization.CanRetrieveCalls
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListCalls(private val persistence: CallPersistence) :
    ListCallsInteractor {

    @CanRetrieveCalls
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListCallsException::class)
    override fun list(status: CallStatus?): List<IdNamePair> =
        persistence.listCalls(status)
}
