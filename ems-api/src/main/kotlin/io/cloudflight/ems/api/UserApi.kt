package io.cloudflight.ems.api

import io.cloudflight.ems.api.dto.OutputUser
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Api("User")
@RequestMapping("/api")
interface UserApi {

    @ApiOperation("Returns the current user")
    @GetMapping("/user")
    fun getCurrentUser(): OutputUser
}
