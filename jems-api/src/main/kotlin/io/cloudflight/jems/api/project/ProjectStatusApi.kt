package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.ApplicationActionInfoDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
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
@RequestMapping("/api/project/{id}/")
interface ProjectStatusApi {

    @ApiOperation("Submit project application")
    @PutMapping("submit")
    fun submitApplication(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Set project application as eligible")
    @PutMapping("set-as-eligible", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setApplicationAsEligible(
        @PathVariable id: Long,
        @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("Set project application as ineligible")
    @PutMapping("set-as-ineligible", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setApplicationAsIneligible(
        @PathVariable id: Long,
        @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("Approve project application")
    @PutMapping("approve", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun approveApplication(
        @PathVariable id: Long,
        @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("Approve project application with condition")
    @PutMapping("approve-with-conditions", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun approveApplicationWithCondition(
        @PathVariable id: Long,
        @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("Refuse project application")
    @PutMapping("refuse", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun refuseApplication(
        @PathVariable id: Long,
        @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("Return project application to applicant")
    @PutMapping("return-to-applicant")
    fun returnApplicationToApplicant(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Start second step")
    @PutMapping("start-second-step")
    fun startSecondStep(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Set quality assessment result to project application")
    @PostMapping("assessment/quality", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setQualityAssessment(
        @PathVariable id: Long,
        @Valid @RequestBody data: InputProjectQualityAssessment
    ): ProjectDetailDTO

    @ApiOperation("Set eligibility assessment result to project application")
    @PostMapping("assessment/eligibility", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setEligibilityAssessment(
        @PathVariable id: Long,
        @Valid @RequestBody data: InputProjectEligibilityAssessment
    ): ProjectDetailDTO

    @ApiOperation("Recheck possibility to revert the last decision made (eligibility decision, funding decision)")
    @GetMapping("status-to-revert-to")
    fun findPossibleDecisionRevertStatus(@PathVariable id: Long): ApplicationStatusDTO?

    @ApiOperation("Revert last decision made (eligibility decision, funding decision)")
    @PostMapping("revert")
    fun revertApplicationDecision(@PathVariable id: Long): ApplicationStatusDTO

}
