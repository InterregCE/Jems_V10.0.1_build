package io.cloudflight.jems.server.user.repository.userrole

import io.cloudflight.jems.server.user.entity.UserRoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRoleRepository : JpaRepository<UserRoleEntity, Long> {

    fun findByName(name: String): Optional<UserRoleEntity>

}
