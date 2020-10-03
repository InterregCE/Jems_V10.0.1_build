package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.entity.Call
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CallRepository: PagingAndSortingRepository<Call, Long> {

    fun findAllByStatus(status: CallStatus, pageable: Pageable): Page<Call>

    fun findOneByName(name: String): Call?

}
