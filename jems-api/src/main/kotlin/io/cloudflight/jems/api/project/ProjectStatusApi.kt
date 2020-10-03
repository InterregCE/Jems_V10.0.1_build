package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.InputProjectStatus
import io.cloudflight.jems.api.project.dto.InputRevertProjectStatus
import io.cloudflight.jems.api.project.dto.OutputProject
import io.cloudflight.jems.api.project.dto.status.OutputRevertProjectStatus
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
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

    @ApiOperation("Recheck possibility to revert the last decision made (eligibility decision, funding decision)")
    @GetMapping("/revert")
    fun findPossibleDecisionRevertStatus(@PathVariable id: Long): OutputRevertProjectStatus?

    @ApiOperation("Revert last decision made (eligibility decision, funding decision)")
    @PostMapping("/revert", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun revertLastDecision(@PathVariable id: Long, @Valid @RequestBody data: InputRevertProjectStatus): OutputProject

}
