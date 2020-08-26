package io.cloudflight.ems.user.service

import io.cloudflight.ems.api.user.dto.InputUserProfile
import io.cloudflight.ems.api.user.dto.OutputUserProfile
import io.cloudflight.ems.user.repository.UserProfileRepository
import io.cloudflight.ems.security.service.SecurityService
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
        val userId = securityService.currentUser!!.user.id!!
        return userProfileRepository.save(profileData.toEntity(userId)).toOutputUserProfile()
    }

    override fun getUserProfile(): OutputUserProfile? {
        val userId = securityService.currentUser!!.user.id!!
        return userProfileRepository.findByIdOrNull(userId)?.toOutputUserProfile()
    }
}
