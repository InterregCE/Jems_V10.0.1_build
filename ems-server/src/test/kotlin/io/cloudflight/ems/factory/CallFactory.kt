package io.cloudflight.ems.factory

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.entity.Call
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.repository.CallRepository
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import javax.transaction.Transactional

@Component
class CallFactory(
    val callRepository: CallRepository
) {

    private val callName = "call name"

    val callStart = ZonedDateTime.now().plusDays(1)
    val callEnd = ZonedDateTime.now().plusDays(20)

    @Transactional
    fun savePublishedCall(user: User): Call {
        val call = callRepository.findOneByName(callName)
        if (call != null)
            return call
        return callRepository.save(Call(null, user, callName, callStart, callEnd, CallStatus.PUBLISHED))
    }

}
