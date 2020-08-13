package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.user.InputUserProfile
import io.cloudflight.ems.api.dto.user.OutputUserProfile
import io.cloudflight.ems.entity.UserProfile

fun InputUserProfile.toEntity(userId: Long) = UserProfile(
    id = userId,
    language = this.language
)

fun UserProfile.toOutputUserProfile() = OutputUserProfile(
    language = this.language
)
