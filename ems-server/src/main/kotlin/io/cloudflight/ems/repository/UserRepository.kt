package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : PagingAndSortingRepository<User, Long> {

    @EntityGraph(attributePaths = ["userRole"])
    fun findOneByEmail(email: String): User?

    fun findOneById(id: Long): User?

}
