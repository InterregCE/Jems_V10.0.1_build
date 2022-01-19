package io.cloudflight.jems.server.factory

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.call.defaultAllowedRealCostsByCallType
import io.cloudflight.jems.server.call.repository.CallRepository
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
    fun savePublishedCallWithoutPolicy(user: UserEntity): CallEntity {
        val call = callRepository.findFirstByName(callName)
        if (call != null)
            return call
        return callRepository.save(
            CallEntity(
                0,
                creator = user,
                name = callName,
                type = CallType.STANDARD,
                prioritySpecificObjectives = mutableSetOf(),
                strategies = mutableSetOf(),
                isAdditionalFundAllowed = false,
                funds = mutableSetOf(),
                startDate = callStart,
                endDateStep1 = null,
                endDate = callEnd,
                status = CallStatus.PUBLISHED,
                lengthOfPeriod = 1,
                allowedRealCosts = defaultAllowedRealCostsByCallType(CallType.STANDARD),
                preSubmissionCheckPluginKey = null
            )
        )
    }

}
