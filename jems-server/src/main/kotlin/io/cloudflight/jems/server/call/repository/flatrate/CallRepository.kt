package io.cloudflight.jems.server.call.repository.flatrate

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.entity.CallEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CallRepository: JpaRepository<CallEntity, Long> {

    fun findAllByStatus(status: CallStatus, pageable: Pageable): Page<CallEntity>

    fun findOneByName(name: String): CallEntity?

    fun existsByStatus(status: CallStatus): Boolean

}
