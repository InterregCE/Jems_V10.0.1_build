package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
interface CallRepository: JpaRepository<CallEntity, Long> {

    fun findAllByStatusAndEndDateAfter(status: CallStatus, after: ZonedDateTime, pageable: Pageable): Page<CallEntity>

    fun findFirstByName(name: String): CallEntity?

    fun existsByStatus(status: CallStatus): Boolean

    fun existsByidAndStatus(callId: Long, status: CallStatus): Boolean

    fun findAllByStatus(status: CallStatus): List<CallEntity>
}
