package io.cloudflight.jems.api.call

import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormFieldConfigurationDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.UpdateApplicationFormFieldConfigurationRequestDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Api("ApplicationFormFieldConfigurations")
@RequestMapping("/api/call/{callId}/applicationFormFieldConfigurations")
interface ApplicationFormConfigurationApi {

    @ApiOperation("Returns set of application form field configurations by call id")
    @GetMapping("")
    fun getByCallId(@PathVariable callId: Long): MutableSet<ApplicationFormFieldConfigurationDTO>


    @ApiOperation("Update application form field configurations")
    @PostMapping("")
    fun update(
        @PathVariable callId: Long,
        @RequestBody applicationFormFieldConfigurations: MutableSet<UpdateApplicationFormFieldConfigurationRequestDTO>
    ) : CallDetailDTO
}
