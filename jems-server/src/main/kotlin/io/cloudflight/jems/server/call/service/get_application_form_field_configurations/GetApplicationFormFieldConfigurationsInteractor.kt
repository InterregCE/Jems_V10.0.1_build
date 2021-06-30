package io.cloudflight.jems.server.call.service.get_application_form_field_configurations

import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration

interface GetApplicationFormFieldConfigurationsInteractor {

    fun get(callId: Long): MutableSet<ApplicationFormFieldConfiguration>

}
