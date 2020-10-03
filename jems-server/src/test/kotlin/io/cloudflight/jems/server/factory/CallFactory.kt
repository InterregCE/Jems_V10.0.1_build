package io.cloudflight.jems.server.factory

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.call.repository.CallRepository
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import javax.transaction.Transactional

@Component
class CallFactory(
    val callRepository: CallRepository
) {

    private val callName = "call name"

    val callStart = ZonedDateTime.now().minusDays(1)
    val callEnd = ZonedDateTime.now().plusDays(20)

    @Transactional
    fun savePublishedCallWithoutPolicy(user: User): Call {
        val call = callRepository.findOneByName(callName)
        if (call != null)
            return call
        return callRepository.save(
            Call(
                null,
                user,
                callName,
                emptySet(),
                emptySet(),
                emptySet(),
                callStart,
                callEnd,
                CallStatus.PUBLISHED,
                lengthOfPeriod = 1
            )
        )
    }

}
