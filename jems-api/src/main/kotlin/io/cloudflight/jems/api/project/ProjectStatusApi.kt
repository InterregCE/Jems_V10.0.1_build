package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.plugin.dto.PreConditionCheckResultDTO
import io.cloudflight.jems.api.project.dto.ApplicationActionInfoDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.ProjectModificationCreateDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ProjectModificationDecisionDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Status")
interface ProjectStatusApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_STATUS = "/api/project/{id}"
    }

    @ApiOperation("execute pre condition checks for project application")
    @GetMapping("$ENDPOINT_API_PROJECT_STATUS/preCheck")
    fun preConditionCheck(@PathVariable id: Long): PreConditionCheckResultDTO

    @ApiOperation("Submit project application")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/submit")
    fun submitApplication(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Set project application as eligible")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/set-as-eligible", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setApplicationAsEligible(
        @PathVariable id: Long,
        @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("Set project application as ineligible")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/set-as-ineligible", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setApplicationAsIneligible(
        @PathVariable id: Long,
        @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("Approve project application")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/approve", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun approveApplication(
        @PathVariable id: Long,
        @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("Approve project application with condition")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/approve-with-conditions", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun approveApplicationWithCondition(
        @PathVariable id: Long,
        @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("Refuse project application")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/refuse", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun refuseApplication(
        @PathVariable id: Long,
        @RequestBody actionInfo: ApplicationActionInfoDTO
    ): ApplicationStatusDTO

    @ApiOperation("Return project application to applicant")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/return-to-applicant")
    fun returnApplicationToApplicant(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Start the modification process")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/start-modification")
    fun startModification(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Return project application to applicant for conditions")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/hand-back-to-applicant")
    fun handBackToApplicant(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Start second step")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/start-second-step")
    fun startSecondStep(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Set quality assessment result to project application")
    @PostMapping("$ENDPOINT_API_PROJECT_STATUS/assessment/quality", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setQualityAssessment(
        @PathVariable id: Long,
        @RequestBody data: ProjectAssessmentQualityDTO
    ): ProjectDetailDTO

    @ApiOperation("Set eligibility assessment result to project application")
    @PostMapping("$ENDPOINT_API_PROJECT_STATUS/assessment/eligibility", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setEligibilityAssessment(
        @PathVariable id: Long,
        @RequestBody data: ProjectAssessmentEligibilityDTO
    ): ProjectDetailDTO

    @ApiOperation("Recheck possibility to revert the last decision made (eligibility decision, funding decision)")
    @GetMapping("$ENDPOINT_API_PROJECT_STATUS/status-to-revert-to")
    fun findPossibleDecisionRevertStatus(@PathVariable id: Long): ApplicationStatusDTO?

    @ApiOperation("Revert last decision made (eligibility decision, funding decision)")
    @PostMapping("$ENDPOINT_API_PROJECT_STATUS/revert")
    fun revertApplicationDecision(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("Get list of all modification decisions")
    @GetMapping("$ENDPOINT_API_PROJECT_STATUS/modification-decisions")
    fun getModificationDecisions(@PathVariable id: Long): List<ProjectModificationDecisionDTO>

    @ApiOperation("approve application modification")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/approve-modification", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun approveModification(
        @PathVariable id: Long, @RequestBody modification: ProjectModificationCreateDTO
    ): ApplicationStatusDTO

    @ApiOperation("reject application modification")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/reject", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun rejectModification(
        @PathVariable id: Long, @RequestBody modification: ProjectModificationCreateDTO
    ): ApplicationStatusDTO

    @ApiOperation("set application to contracted")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/set-to-contracted")
    fun setToContracted(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("set application to closed")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/set-to-closed")
    fun setToClosed(@PathVariable id: Long): ApplicationStatusDTO

    @ApiOperation("revert application to contracted")
    @PutMapping("$ENDPOINT_API_PROJECT_STATUS/revert-to-contracted")
    fun revertToContracted(@PathVariable id: Long): ApplicationStatusDTO

}
