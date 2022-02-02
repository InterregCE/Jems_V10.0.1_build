package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.programme.ProgrammeInfoData
import io.cloudflight.jems.plugin.contract.services.ProgrammeDataProvider
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProgrammeDataProviderImpl(
    private val persistence: ProgrammeDataPersistence
) : ProgrammeDataProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(ProgrammeDataProviderImpl::class.java)
    }

    override fun getProgrammeData(): ProgrammeInfoData =
        persistence.getProgrammeData().toDataModel().also {
            logger.info("Retrieved programme data for programme via plugin.")
        }
}
