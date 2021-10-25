package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ResultIndicatorRepository : JpaRepository<ResultIndicatorEntity, Long> {

    @EntityGraph(attributePaths = ["programmePriorityPolicyEntity.programmePriority"])
    override fun findAll(pageable: Pageable): Page<ResultIndicatorEntity>

    @EntityGraph(attributePaths = ["programmePriorityPolicyEntity.programmePriority"])
    override fun findById(id: Long): Optional<ResultIndicatorEntity>


    fun findTop50ByOrderById(): List<ResultIndicatorEntity>
    fun findOneByIdentifier(identifier: String): ResultIndicatorEntity?
    fun findAllByProgrammePriorityPolicyEntityProgrammeObjectivePolicyOrderById(programmeObjectivePolicy: ProgrammeObjectivePolicy): Iterable<ResultIndicatorEntity>

}
