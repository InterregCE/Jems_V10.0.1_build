package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.ApplicationFormConfigurationApi
import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormConfigurationDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormConfigurationSummaryDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.UpdateApplicationFormConfigurationRequestDTO
import io.cloudflight.jems.server.call.service.get_application_form_configuration.GetApplicationFormConfigurationInteractor
import io.cloudflight.jems.server.call.service.list_application_form_configurations.ListApplicationFormConfigurationInteractor
import io.cloudflight.jems.server.call.service.update_application_form_configuration.UpdateApplicationFormConfigurationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ApplicationFormConfigurationController(
    private val getApplicationFormConfiguration: GetApplicationFormConfigurationInteractor,
    private val listApplicationFormConfiguration: ListApplicationFormConfigurationInteractor,
    private val updateApplicationFormConfiguration: UpdateApplicationFormConfigurationInteractor
) : ApplicationFormConfigurationApi {

    override fun getById(id: Long): ApplicationFormConfigurationDTO =
        getApplicationFormConfiguration.get(id).toDTO()

    override fun update(applicationFormConfiguration: UpdateApplicationFormConfigurationRequestDTO): Unit =
        updateApplicationFormConfiguration.update(applicationFormConfiguration.toModel())

    override fun list(): List<ApplicationFormConfigurationSummaryDTO> =
        listApplicationFormConfiguration.list().toDTO()

}
