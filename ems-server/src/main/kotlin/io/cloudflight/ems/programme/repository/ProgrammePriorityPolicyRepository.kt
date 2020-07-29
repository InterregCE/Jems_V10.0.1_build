package io.cloudflight.ems.programme.repository

import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import io.cloudflight.ems.programme.entity.ProgrammePriorityPolicy
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProgrammePriorityPolicyRepository :
    PagingAndSortingRepository<ProgrammePriorityPolicy, ProgrammeObjectivePolicy> {

    fun findFirstByCode(code: String): ProgrammePriorityPolicy?

    @Query(
        nativeQuery = true, value = "SELECT programme_priority_id " +
            "FROM programme_priority_policy WHERE programme_objective_policy_code = :#{#policy.name()}"
    )
    fun getPriorityIdForPolicyIfExists(@Param("policy") policy: ProgrammeObjectivePolicy): Long?

}
