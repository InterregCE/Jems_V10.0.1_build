package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserEntityRepository: CrudRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
}
