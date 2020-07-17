package io.cloudflight.ems.api

import io.cloudflight.ems.api.dto.InputProjectEligibilityAssessment
import io.cloudflight.ems.api.dto.InputProjectQualityAssessment
import io.cloudflight.ems.api.dto.InputProjectStatus
import io.cloudflight.ems.api.dto.OutputProject
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Project Status")
@RequestMapping("/api/project/{id}/status")
interface ProjectStatusApi {

    @ApiOperation("Change status of project application")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setProjectStatus(@PathVariable id: Long, @Valid @RequestBody status: InputProjectStatus): OutputProject

    @ApiOperation("Set quality assessment result to project application")
    @PostMapping("/quality", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setQualityAssessment(@PathVariable id: Long, @Valid @RequestBody data: InputProjectQualityAssessment): OutputProject

    @ApiOperation("Set eligibility assessment result to project application")
    @PostMapping("/eligibility", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setEligibilityAssessment(@PathVariable id: Long, @Valid @RequestBody data: InputProjectEligibilityAssessment): OutputProject
}
