package io.cloudflight.jems.server.call.service.update_application_form_configuration

import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration

interface UpdateApplicationFormConfigurationInteractor {
    fun update(applicationFormConfiguration: ApplicationFormConfiguration)
}
