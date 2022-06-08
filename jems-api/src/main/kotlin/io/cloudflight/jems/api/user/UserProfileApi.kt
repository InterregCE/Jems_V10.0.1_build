package io.cloudflight.jems.api.user

import io.cloudflight.jems.api.user.dto.InputUserProfile
import io.cloudflight.jems.api.user.dto.OutputUserProfile
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid

@Api("User Profile")
interface UserProfileApi {

    companion object {
        private const val ENDPOINT_API_USER_PROFILE = "/api/profile"
    }

    @ApiOperation("Updates user profile")
    @PutMapping(ENDPOINT_API_USER_PROFILE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateUserProfile(@Valid @RequestBody profileData: InputUserProfile): OutputUserProfile

    @ApiOperation("Gets user profile")
    @GetMapping(ENDPOINT_API_USER_PROFILE)
    fun getUserProfile(): OutputUserProfile?

}
