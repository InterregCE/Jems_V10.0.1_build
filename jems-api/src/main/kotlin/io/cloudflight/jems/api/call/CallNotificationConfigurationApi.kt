package io.cloudflight.jems.api.call

import io.cloudflight.jems.api.call.dto.notificationConfiguration.ProjectNotificationConfigurationDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("CallNotificationConfiguration")
interface CallNotificationConfigurationApi {
    companion object {
        private const val ENDPOINT_API_CALL_NOTIFICATION_CONFIG = "/api/call/{callId}/notificationConfigurations"
    }

    @ApiOperation("Returns set of project notification configurations by call id")
    @GetMapping("$ENDPOINT_API_CALL_NOTIFICATION_CONFIG/project")
    fun getProjectNotificationsByCallId(@PathVariable callId: Long): List<ProjectNotificationConfigurationDTO>

    @ApiOperation("Update project notification configurations")
    @PostMapping("$ENDPOINT_API_CALL_NOTIFICATION_CONFIG/project", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectNotifications(
        @PathVariable callId: Long,
        @RequestBody projectNotificationConfigurations: List<ProjectNotificationConfigurationDTO>
    ) : List<ProjectNotificationConfigurationDTO>

    @ApiOperation("Returns set of project notification configurations by call id")
    @GetMapping("$ENDPOINT_API_CALL_NOTIFICATION_CONFIG/partnerReport")
    fun getPartnerReportNotificationsByCallId(@PathVariable callId: Long): List<ProjectNotificationConfigurationDTO>

    @ApiOperation("Update project notification configurations")
    @PostMapping("$ENDPOINT_API_CALL_NOTIFICATION_CONFIG/partnerReport", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePartnerReportNotifications(
        @PathVariable callId: Long,
        @RequestBody projectNotificationConfigurations: List<ProjectNotificationConfigurationDTO>
    ) : List<ProjectNotificationConfigurationDTO>


}
