package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.user.UserProfileApi
import io.cloudflight.jems.api.user.dto.InputUserProfile
import io.cloudflight.jems.api.user.dto.OutputUserProfile
import io.cloudflight.jems.server.user.service.UserProfileService
import org.springframework.web.bind.annotation.RestController

@RestController
class UserProfileController(
    val userProfileService: UserProfileService
) : UserProfileApi {

    override fun updateUserProfile(profileData: InputUserProfile): OutputUserProfile {
        return this.userProfileService.setUserProfile(profileData)
    }

    override fun getUserProfile(): OutputUserProfile? {
        return this.userProfileService.getUserProfile()
    }

}
