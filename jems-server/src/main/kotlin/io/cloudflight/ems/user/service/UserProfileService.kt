package io.cloudflight.ems.user.service

import io.cloudflight.ems.api.user.dto.InputUserProfile
import io.cloudflight.ems.api.user.dto.OutputUserProfile

interface UserProfileService {

    fun setUserProfile(profileData: InputUserProfile): OutputUserProfile

    fun getUserProfile(): OutputUserProfile?
}
