package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.user.InputUserProfile
import io.cloudflight.ems.api.dto.user.OutputUserProfile

interface UserProfileService {

    fun setUserProfile(profileData: InputUserProfile): OutputUserProfile

    fun getUserProfile(): OutputUserProfile?
}
