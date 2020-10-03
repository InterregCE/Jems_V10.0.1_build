package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.user.entity.UserProfile
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserProfileRepository : PagingAndSortingRepository<UserProfile, Long>
