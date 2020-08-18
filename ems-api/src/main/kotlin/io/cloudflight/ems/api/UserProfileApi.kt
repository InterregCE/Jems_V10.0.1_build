package io.cloudflight.ems.api

import io.cloudflight.ems.api.dto.user.InputUserProfile
import io.cloudflight.ems.api.dto.user.OutputUserProfile
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("User Profile")
@RequestMapping("/api/profile")
interface UserProfileApi {


    @ApiOperation("Updates user profile")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateUserProfile(@Valid @RequestBody profileData: InputUserProfile): OutputUserProfile

    @ApiOperation("Gets user profile")
    @GetMapping()
    fun getUserProfile(): OutputUserProfile?

}
