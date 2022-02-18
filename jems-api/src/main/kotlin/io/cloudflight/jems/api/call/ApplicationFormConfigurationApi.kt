package io.cloudflight.jems.api.call

import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.ApplicationFormFieldConfigurationDTO
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.UpdateApplicationFormFieldConfigurationRequestDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("ApplicationFormFieldConfigurations")
interface ApplicationFormConfigurationApi {

    companion object {
        private const val ENDPOINT_API_CALL_FIELD_CONFIG = "/api/call/{callId}/applicationFormFieldConfigurations"
    }

    @ApiOperation("Returns set of application form field configurations by call id")
    @GetMapping(ENDPOINT_API_CALL_FIELD_CONFIG)
    fun getByCallId(@PathVariable callId: Long): MutableSet<ApplicationFormFieldConfigurationDTO>


    @ApiOperation("Update application form field configurations")
    @PostMapping(ENDPOINT_API_CALL_FIELD_CONFIG, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(
        @PathVariable callId: Long,
        @RequestBody applicationFormFieldConfigurations: MutableSet<UpdateApplicationFormFieldConfigurationRequestDTO>
    ) : CallDetailDTO
}
