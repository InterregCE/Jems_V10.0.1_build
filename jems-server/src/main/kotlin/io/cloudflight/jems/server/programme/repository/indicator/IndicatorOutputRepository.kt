package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface IndicatorOutputRepository : PagingAndSortingRepository<IndicatorOutput, Long> {

    @EntityGraph(attributePaths = ["programmePriorityPolicy.programmePriority"])
    override fun findAll(pageable: Pageable): Page<IndicatorOutput>

    @EntityGraph(attributePaths = ["programmePriorityPolicy.programmePriority"])
    override fun findById(id: Long): Optional<IndicatorOutput>

    fun existsByIdentifier(identifier: String): Boolean
    fun findOneByIdentifier(identifier: String): IndicatorOutput?

}
