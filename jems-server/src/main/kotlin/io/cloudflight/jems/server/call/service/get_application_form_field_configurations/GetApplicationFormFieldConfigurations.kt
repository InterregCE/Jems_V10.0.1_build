package io.cloudflight.jems.server.call.service.get_application_form_field_configurations

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetApplicationFormFieldConfigurations(private val persistence: CallPersistence) :
    GetApplicationFormFieldConfigurationsInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetApplicationFormConfigurationException::class)
    override fun get(callId: Long): MutableSet<ApplicationFormFieldConfiguration> =
        persistence.getApplicationFormFieldConfigurations(callId)

}
