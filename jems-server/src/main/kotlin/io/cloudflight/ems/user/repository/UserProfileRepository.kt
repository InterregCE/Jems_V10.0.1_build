package io.cloudflight.ems.user.repository

import io.cloudflight.ems.user.entity.UserProfile
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserProfileRepository : PagingAndSortingRepository<UserProfile, Long>
