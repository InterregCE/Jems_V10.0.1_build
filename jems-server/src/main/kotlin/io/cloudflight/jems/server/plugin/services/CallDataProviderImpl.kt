package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.call.CallDetailData
import io.cloudflight.jems.plugin.contract.services.CallDataProvider
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.get_call.GetCallByProjectIdException
import io.cloudflight.jems.server.call.service.get_call.GetCallException
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.language.ProgrammeLanguagePersistence
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CallDataProviderImpl(
    private val persistence: CallPersistence,
    private val programmeLanguagePersistence: ProgrammeLanguagePersistence
) : CallDataProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(CallDataProviderImpl::class.java)
    }

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallException::class)
    override fun getCallData(callId: Long): CallDetailData =
        persistence.getCallById(callId).toDataModel(getInputLanguages()).also {
            logger.info("Retrieved call data for call id=$callId via plugin.")
        }

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallByProjectIdException::class)
    override fun getCallDataByProjectId(projectId: Long): CallDetailData =
        persistence.getCallByProjectId(projectId).toDataModel(getInputLanguages()).also {
            logger.info("Retrieved call data for project id=$projectId via plugin.")
        }

    private fun getInputLanguages(): List<ProgrammeLanguage> =
        programmeLanguagePersistence.getLanguages().filter { it.input }
}
