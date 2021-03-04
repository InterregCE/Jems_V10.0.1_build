package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.user.entity.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    @EntityGraph(attributePaths = ["userRole"])
    fun findOneByEmail(email: String): User?

    fun findOneById(id: Long): User?

}
