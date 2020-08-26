package io.cloudflight.ems.user.service

import io.cloudflight.ems.api.user.dto.InputUserProfile
import io.cloudflight.ems.api.user.dto.OutputUserProfile
import io.cloudflight.ems.user.entity.UserProfile

fun InputUserProfile.toEntity(userId: Long) = UserProfile(
    id = userId,
    language = this.language
)

fun UserProfile.toOutputUserProfile() = OutputUserProfile(
    language = this.language
)
