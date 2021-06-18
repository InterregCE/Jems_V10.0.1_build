package io.cloudflight.jems.server.call.service.list_application_form_configurations

import io.cloudflight.jems.server.call.service.model.ApplicationFormConfigurationSummary

interface ListApplicationFormConfigurationInteractor {

    fun list(): List<ApplicationFormConfigurationSummary>

}
