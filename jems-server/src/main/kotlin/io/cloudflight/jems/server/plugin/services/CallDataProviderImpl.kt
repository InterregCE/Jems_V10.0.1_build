package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.call.CallDetailData
import io.cloudflight.jems.plugin.contract.services.CallDataProvider
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.get_call.GetCallByProjectIdException
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
    override fun getCallData(callId: Long): CallDetailData =
        persistence.getCallById(callId).toDataModel().also {
            logger.info("Retrieved call data for call id=$callId via plugin.")
        }

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallByProjectIdException::class)
    override fun getCallDataByProjectId(projectId: Long): CallDetailData =
        persistence.getCallByProjectId(projectId).toDataModel().also {
            logger.info("Retrieved call data for project id=$projectId via plugin.")
        }

}
