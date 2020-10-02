package io.cloudflight.ems.user.controller

import io.cloudflight.ems.api.user.UserProfileApi
import io.cloudflight.ems.api.user.dto.InputUserProfile
import io.cloudflight.ems.api.user.dto.OutputUserProfile
import io.cloudflight.ems.user.service.UserProfileService
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
