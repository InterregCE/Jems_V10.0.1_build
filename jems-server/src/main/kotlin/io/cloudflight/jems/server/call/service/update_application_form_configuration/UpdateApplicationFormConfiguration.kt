package io.cloudflight.jems.server.call.service.update_application_form_configuration

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateApplicationFormConfiguration(private val persistence: CallPersistence) :
    UpdateApplicationFormConfigurationInteractor {

    //todo access control?
    @Transactional
    @ExceptionWrapper(UpdateApplicationFormConfigurationException::class)
    override fun update(applicationFormConfiguration: ApplicationFormConfiguration): Unit =
        ifConfigurationIsValid(applicationFormConfiguration).run {
            persistence.updateApplicationFormConfigurations(applicationFormConfiguration)
        }

    fun ifConfigurationIsValid(applicationFormConfiguration: ApplicationFormConfiguration) {
        if (applicationFormConfiguration.fieldConfigurations.any {
                !ApplicationFormFieldSetting.getValidVisibilityStatusSetById(it.id).contains(it.visibilityStatus)
            }) throw InvalidFieldStatusException()
    }
}
