package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.plugin.dto.PreConditionCheckResultDTO
import io.cloudflight.jems.api.project.dto.ApplicationActionInfoDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Api("Project Status")
@RequestMapping("/api/project/{id}/")
interface ProjectStatusApi {

    @ApiOperation("execute pre condition checks for project application")
    @GetMapping("preCheck")
    fun preConditionCheck(@PathVariable id: Long): PreConditionCheckResultDTO

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

    @ApiOperation("Start the modification process")
    @PutMapping("start-modification")
    fun startModification(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Return project application to applicant for conditions")
    @PutMapping("hand-back-to-applicant")
    fun handBackToApplicant(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Start second step")
    @PutMapping("start-second-step")
    fun startSecondStep(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Set quality assessment result to project application")
    @PostMapping("assessment/quality", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setQualityAssessment(
        @PathVariable id: Long,
        @RequestBody data: ProjectAssessmentQualityDTO
    ): ProjectDetailDTO

    @ApiOperation("Set eligibility assessment result to project application")
    @PostMapping("assessment/eligibility", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setEligibilityAssessment(
        @PathVariable id: Long,
        @RequestBody data: ProjectAssessmentEligibilityDTO
    ): ProjectDetailDTO

    @ApiOperation("Recheck possibility to revert the last decision made (eligibility decision, funding decision)")
    @GetMapping("status-to-revert-to")
    fun findPossibleDecisionRevertStatus(@PathVariable id: Long): ApplicationStatusDTO?

    @ApiOperation("Revert last decision made (eligibility decision, funding decision)")
    @PostMapping("revert")
    fun revertApplicationDecision(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Get list of all modification decisions")
    @GetMapping("modificationDecisions")
    fun getModificationDecisions(@PathVariable id: Long): List<ProjectStatusDTO>

    @ApiOperation("approve application modification")
    @PutMapping("approve modification", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun approveModification(
        @PathVariable id: Long, @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("reject application modification")
    @PutMapping("reject", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun rejectModification(
        @PathVariable id: Long, @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("set application to contracted")
    @PutMapping("set-to-contracted")
    fun setToContracted(@PathVariable id: Long): ApplicationStatusDTO

}
