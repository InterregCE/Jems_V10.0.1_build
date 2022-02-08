package io.cloudflight.jems.server.call.service.create_call

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.callCreated
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.validator.CallValidator
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateCall(
    private val persistence: CallPersistence,
    private val securityService: SecurityService,
    private val callValidator: CallValidator,
    private val auditPublisher: ApplicationEventPublisher,
) : CreateCallInteractor {

    @CanUpdateCall
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
            persistence.updateProjectCallStateAids(it.id, call.stateAidIds)
            persistence.saveApplicationFormFieldConfigurations(it.id, ApplicationFormFieldSetting.getDefaultApplicationFormFieldConfigurations(it.type))
            // ToDo to be removed when implementing MP2-2210
            if (call.type == CallType.SPF) {
                persistence.updateProjectCallPreSubmissionCheckPlugin(it.id, "jems-pre-condition-check-blocked")
            }
            auditPublisher.publishEvent(callCreated(this, it))
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
