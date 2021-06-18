package io.cloudflight.jems.server.call.service.get_application_form_configuration

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetApplicationFormConfiguration(private val persistence: CallPersistence) :
    GetApplicationFormConfigurationInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetApplicationFormConfigurationException::class)
    override fun get(id: Long): ApplicationFormConfiguration =
        persistence.getApplicationFormConfiguration(id)

}
