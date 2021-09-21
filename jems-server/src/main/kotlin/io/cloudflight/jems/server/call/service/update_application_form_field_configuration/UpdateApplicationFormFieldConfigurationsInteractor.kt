package io.cloudflight.jems.server.call.service.update_application_form_field_configuration

import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.CallDetail


interface UpdateApplicationFormFieldConfigurationsInteractor {
    fun update(callId: Long, applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration>): CallDetail
}
