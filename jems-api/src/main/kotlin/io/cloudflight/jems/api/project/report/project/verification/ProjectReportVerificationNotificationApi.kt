package io.cloudflight.jems.api.project.report.project.verification

import io.cloudflight.jems.api.project.dto.report.project.verification.notification.ProjectReportVerificationNotificationDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Api("Project report verification notification API")
interface ProjectReportVerificationNotificationApi {

    companion object{
        const val ENDPOINT_API_PROJECT_REPORT_VERIFICATION_NOTIFICATION =
            "${ProjectReportVerificationApi.ENDPOINT_API_PROJECT_REPORT_VERIFICATION}/notification"
    }

    @ApiOperation("Get notification data for current report")
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_VERIFICATION_NOTIFICATION)
    fun getLastProjectReportVerificationNotification(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long
    ): ProjectReportVerificationNotificationDTO?

    @ApiOperation("Notify users that verification is done by JS")
    @PostMapping(ENDPOINT_API_PROJECT_REPORT_VERIFICATION_NOTIFICATION)
    fun sendVerificationDoneByJsNotification(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long
    ): ProjectReportVerificationNotificationDTO

}
