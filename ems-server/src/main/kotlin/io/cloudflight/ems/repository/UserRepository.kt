package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.User
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : PagingAndSortingRepository<User, Long> {

    fun findByEmail(email: String): User?

}
