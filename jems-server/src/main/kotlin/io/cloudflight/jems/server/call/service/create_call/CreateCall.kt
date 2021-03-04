package io.cloudflight.jems.server.call.service.create_call

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.authorization.CanUpdateCalls
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.callCreated
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.validator.CallValidator
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateCall(
    private val persistence: CallPersistence,
    private val securityService: SecurityService,
    private val auditService: AuditService,
    private val callValidator: CallValidator,
) : CreateCallInteractor {

    @CanUpdateCalls
    @Transactional
    @ExceptionWrapper(CreateCallException::class)
    override fun createCallInDraft(call: Call): CallDetail {
        callValidator.validateCommonCall(call = call)
        validateUniqueName(callIdWithThisName = persistence.getCallIdForNameIfExists(call.name))
        validateCorrectStatus(call = call)

        return persistence.createCall(
            call = call.apply { status = CallStatus.DRAFT },
            userId = securityService.currentUser?.user?.id!!,
        ).also {
            callCreated(it).logWith(auditService)
        }
    }

    private fun validateUniqueName(callIdWithThisName: Long?) {
        if (callIdWithThisName != null)
            throw CallNameNotUnique()
    }

    private fun validateCorrectStatus(call: Call) {
        if (call.status != null && call.status != CallStatus.DRAFT)
            throw CallCreatedIsNotDraft()
    }

}
