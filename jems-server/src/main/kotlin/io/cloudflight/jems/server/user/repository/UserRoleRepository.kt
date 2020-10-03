package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.user.entity.UserRole
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRoleRepository : PagingAndSortingRepository<UserRole, Long> {

    fun findOneByName(name: String): UserRole?

}
