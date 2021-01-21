package io.cloudflight.jems.server.factory

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.call.repository.flatrate.CallRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Component
class CallFactory(
    val callRepository: CallRepository
) {

    private val callName = "call name"

    val callStart = ZonedDateTime.now().minusDays(1)
    val callEnd = ZonedDateTime.now().plusDays(20)

    @Transactional
    fun savePublishedCallWithoutPolicy(user: User): CallEntity {
        val call = callRepository.findOneByName(callName)
        if (call != null)
            return call
        return callRepository.save(
            CallEntity(
                0,
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
