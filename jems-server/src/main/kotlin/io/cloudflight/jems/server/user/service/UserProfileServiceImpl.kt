package io.cloudflight.jems.server.user.service

import io.cloudflight.jems.api.user.dto.InputUserProfile
import io.cloudflight.jems.api.user.dto.OutputUserProfile
import io.cloudflight.jems.server.user.repository.UserProfileRepository
import io.cloudflight.jems.server.authentication.service.SecurityService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProfileServiceImpl (
    private val userProfileRepository: UserProfileRepository,
    private val securityService: SecurityService
): UserProfileService {

    @Transactional
    override fun setUserProfile(profileData: InputUserProfile): OutputUserProfile {
        val userId = securityService.currentUser!!.user.id
        return userProfileRepository.save(profileData.toEntity(userId)).toOutputUserProfile()
    }

    override fun getUserProfile(): OutputUserProfile? {
        val userId = securityService.currentUser!!.user.id
        return userProfileRepository.findByIdOrNull(userId)?.toOutputUserProfile()
    }
}
