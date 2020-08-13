package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.UserProfile
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserProfileRepository : PagingAndSortingRepository<UserProfile, Long>
