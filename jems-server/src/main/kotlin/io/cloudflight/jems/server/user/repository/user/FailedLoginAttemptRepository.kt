package io.cloudflight.jems.server.user.repository.user

import io.cloudflight.jems.server.user.entity.FailedLoginAttemptEntity
import io.cloudflight.jems.server.user.entity.FailedLoginAttemptId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FailedLoginAttemptRepository : JpaRepository<FailedLoginAttemptEntity, FailedLoginAttemptId>{

    @Query("SELECT * FROM #{#entityName} WHERE email= :email", nativeQuery = true)
    fun findByEmail(email: String) : FailedLoginAttemptEntity?

    @Modifying
    @Query("DELETE FROM #{#entityName} WHERE email= :email", nativeQuery = true)
    fun deleteByEmail(email: String)
}
