package io.cloudflight.jems.api.notification

import io.cloudflight.jems.api.notification.dto.NotificationDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping

@Api("Notification")
interface NotificationApi {

    companion object {
        private const val ENDPOINT_API_NOTIFICATION = "/api/notification"
    }

    @ApiOperation("Returns my notifications")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_NOTIFICATION)
    fun getMyNotifications(pageable: Pageable): Page<NotificationDTO>

}
