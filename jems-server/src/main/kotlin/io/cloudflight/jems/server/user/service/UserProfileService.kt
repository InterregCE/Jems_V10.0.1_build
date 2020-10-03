package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.user.dto.InputUserProfile
import io.cloudflight.jems.api.user.dto.OutputUserProfile

interface UserProfileService {

    fun setUserProfile(profileData: InputUserProfile): OutputUserProfile

    fun getUserProfile(): OutputUserProfile?
}
