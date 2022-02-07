package io.cloudflight.jems.server.call.service.get_application_form_field_configurations

import io.cloudflight.jems.server.call.service.model.CallApplicationFormFieldsConfiguration

interface GetApplicationFormFieldConfigurationsInteractor {

    fun get(callId: Long): CallApplicationFormFieldsConfiguration

}
