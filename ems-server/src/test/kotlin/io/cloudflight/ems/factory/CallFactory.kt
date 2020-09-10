package io.cloudflight.ems.factory

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.call.entity.Call
import io.cloudflight.ems.user.entity.User
import io.cloudflight.ems.call.repository.CallRepository
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
                callStart,
                callEnd,
                CallStatus.PUBLISHED,
                lengthOfPeriod = 1
            )
        )
    }

}
