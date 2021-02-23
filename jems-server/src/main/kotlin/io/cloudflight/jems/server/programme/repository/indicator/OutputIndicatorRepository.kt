package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface OutputIndicatorRepository : JpaRepository<OutputIndicatorEntity, Long> {

    @EntityGraph(attributePaths = ["programmePriorityPolicyEntity.programmePriority"])
    override fun findAll(pageable: Pageable): Page<OutputIndicatorEntity>

    @EntityGraph(attributePaths = ["programmePriorityPolicyEntity.programmePriority"])
    override fun findById(id: Long): Optional<OutputIndicatorEntity>

    fun findTop50ByOrderById(): List<OutputIndicatorEntity>
    fun findOneByIdentifier(identifier: String): OutputIndicatorEntity?
    fun findAllByProgrammePriorityPolicyEntityProgrammeObjectivePolicyOrderById(programmeObjectivePolicy: ProgrammeObjectivePolicy): Iterable<OutputIndicatorEntity>

}
