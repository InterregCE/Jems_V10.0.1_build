package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.user.dto.InputUserProfile
import io.cloudflight.jems.api.user.dto.OutputUserProfile
import io.cloudflight.jems.server.user.entity.UserProfile

fun InputUserProfile.toEntity(userId: Long) = UserProfile(
    id = userId,
    language = this.language
)

fun UserProfile.toOutputUserProfile() = OutputUserProfile(
    language = this.language
)
