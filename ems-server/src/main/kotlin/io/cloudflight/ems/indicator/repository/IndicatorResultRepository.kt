package io.cloudflight.ems.indicator.repository

import io.cloudflight.ems.indicator.entity.IndicatorResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface IndicatorResultRepository : PagingAndSortingRepository<IndicatorResult, Long> {

    @EntityGraph(attributePaths = ["programmePriorityPolicy.programmePriority"])
    override fun findAll(pageable: Pageable): Page<IndicatorResult>

    @EntityGraph(attributePaths = ["programmePriorityPolicy.programmePriority"])
    override fun findById(id: Long): Optional<IndicatorResult>

    fun existsByIdentifier(identifier: String): Boolean

}
