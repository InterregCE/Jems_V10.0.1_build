package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.ApplicationFormConfigurationApi
import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.ApplicationFormFieldConfigurationDTO
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.UpdateApplicationFormFieldConfigurationRequestDTO
import io.cloudflight.jems.server.call.service.get_application_form_field_configurations.GetApplicationFormFieldConfigurationsInteractor
import io.cloudflight.jems.server.call.service.update_application_form_field_configuration.UpdateApplicationFormFieldConfigurationsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ApplicationFormConfigurationController(
    private val getApplicationFormFieldConfigurations: GetApplicationFormFieldConfigurationsInteractor,
    private val updateApplicationFormFieldConfigurations: UpdateApplicationFormFieldConfigurationsInteractor
) : ApplicationFormConfigurationApi {

    override fun getByCallId(callId: Long): MutableSet<ApplicationFormFieldConfigurationDTO> =
        getApplicationFormFieldConfigurations.get(callId).toDTO()

    override fun update(callId: Long, applicationFormFieldConfigurations: MutableSet<UpdateApplicationFormFieldConfigurationRequestDTO>): CallDetailDTO =
        updateApplicationFormFieldConfigurations.update(callId, applicationFormFieldConfigurations.toModel()).toDto()

}
