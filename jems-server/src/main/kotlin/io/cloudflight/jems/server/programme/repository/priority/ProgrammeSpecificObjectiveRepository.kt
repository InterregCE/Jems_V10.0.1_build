package io.cloudflight.jems.server.programme.repository.priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeSpecificObjectiveRepository :
    PagingAndSortingRepository<ProgrammeSpecificObjectiveEntity, ProgrammeObjectivePolicy> {

    // max = ProgrammeObjectivePolicy enum size
    fun findTop45ByOrderByProgrammeObjectivePolicy(): Iterable<ProgrammeSpecificObjectiveEntity>

    override fun findAll(): MutableIterable<ProgrammeSpecificObjectiveEntity> =
        throw UnsupportedOperationException("use findTop45ByOrderByProgrammeObjectivePolicy")

    @Query(
        nativeQuery = true, value = "SELECT programme_priority_id " +
            "FROM programme_priority_specific_objective WHERE programme_objective_policy_code = :#{#policy.name()}"
    )
    fun getPriorityIdForPolicyIfExists(@Param("policy") policy: ProgrammeObjectivePolicy): Long?

    fun findAllByCodeIn(codes: Collection<String>): Iterable<ProgrammeSpecificObjectiveEntity>

    @Query(
        nativeQuery = true,
        value = "SELECT DISTINCT programme_specific_objective FROM project_call_priority_specific_objective"
    )
    fun getObjectivePoliciesAlreadyInUse(): Iterable<String>

}
