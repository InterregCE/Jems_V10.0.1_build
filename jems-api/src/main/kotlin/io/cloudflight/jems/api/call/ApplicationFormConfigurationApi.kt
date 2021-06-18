package io.cloudflight.jems.api.call

import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormConfigurationDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormConfigurationSummaryDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.UpdateApplicationFormConfigurationRequestDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Api("ApplicationFormConfiguration")
@RequestMapping("/api/call/applicationFormConfiguration")
interface ApplicationFormConfigurationApi {

    @ApiOperation("Returns list of application form configuration summary")
    @GetMapping("")
    fun list(): List<ApplicationFormConfigurationSummaryDTO>

    @ApiOperation("Returns an application form configuration by id")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApplicationFormConfigurationDTO


    @ApiOperation("Update an application form configuration")
    @PostMapping("")
    fun update(@RequestBody applicationFormConfiguration: UpdateApplicationFormConfigurationRequestDTO)
}
