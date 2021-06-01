package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.call.CallDetailData
import io.cloudflight.jems.plugin.contract.services.CallDataProvider
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.get_call.GetCallException
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CallDataProviderImpl(
    private val persistence: CallPersistence
) : CallDataProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(CallDataProviderImpl::class.java)
    }

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallException::class)
    override fun getCallDataForCallId(callId: Long): CallDetailData {
        val call = persistence.getCallById(callId)

        logger.info("Retrieved call data for call id=$callId via plugin.")

        return call.toDataModel()
    }
}