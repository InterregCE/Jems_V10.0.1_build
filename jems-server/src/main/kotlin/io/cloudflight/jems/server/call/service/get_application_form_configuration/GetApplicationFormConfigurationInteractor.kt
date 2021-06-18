package io.cloudflight.jems.server.call.service.get_application_form_configuration

import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration

interface GetApplicationFormConfigurationInteractor {

    fun get(id: Long): ApplicationFormConfiguration

}
