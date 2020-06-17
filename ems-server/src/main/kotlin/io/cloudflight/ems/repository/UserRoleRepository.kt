package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.UserRole
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRoleRepository : PagingAndSortingRepository<UserRole, Long> {
    fun findOneByName(name: String): UserRole?
}
