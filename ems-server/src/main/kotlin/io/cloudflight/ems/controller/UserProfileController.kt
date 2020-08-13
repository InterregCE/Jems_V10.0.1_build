package io.cloudflight.ems.controller

import io.cloudflight.ems.api.UserProfileApi
import io.cloudflight.ems.api.dto.user.InputUserProfile
import io.cloudflight.ems.api.dto.user.OutputUserProfile
import io.cloudflight.ems.service.UserProfileService
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
