package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.entity.indicator.IndicatorResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface IndicatorResultRepository : JpaRepository<IndicatorResult, Long> {

    @EntityGraph(attributePaths = ["programmePriorityPolicy.programmePriority"])
    override fun findAll(pageable: Pageable): Page<IndicatorResult>

    @EntityGraph(attributePaths = ["programmePriorityPolicy.programmePriority"])
    override fun findById(id: Long): Optional<IndicatorResult>

    fun existsByIdentifier(identifier: String): Boolean
    fun findOneByIdentifier(identifier: String): IndicatorResult?
    fun findAllByProgrammePriorityPolicyProgrammeObjectivePolicyOrderById(programmeObjectivePolicy: ProgrammeObjectivePolicy): Iterable<IndicatorResult>
}
