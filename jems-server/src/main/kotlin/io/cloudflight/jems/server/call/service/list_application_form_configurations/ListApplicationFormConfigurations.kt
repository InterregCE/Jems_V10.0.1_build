package io.cloudflight.jems.server.call.service.list_application_form_configurations

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfigurationSummary
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListApplicationFormConfigurations(private val persistence: CallPersistence) :
    ListApplicationFormConfigurationInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(ListApplicationFormConfigurationsException::class)
    override fun list(): List<ApplicationFormConfigurationSummary> =
        persistence.listApplicationFormConfigurations()
}
