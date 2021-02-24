package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

interface CustomResultIndicatorRepository {
    fun getReferenceIfExistsOrThrow(id: Long?): ResultIndicatorEntity?
}

open class CustomResultIndicatorRepositoryImpl(val repository: ResultIndicatorRepository) :
    CustomResultIndicatorRepository {
    @Transactional(readOnly = true)
    override fun getReferenceIfExistsOrThrow(id: Long?): ResultIndicatorEntity? {
        var resultIndicatorEntity: ResultIndicatorEntity? = null
        if (id != null && id != 0L)
            runCatching {
                resultIndicatorEntity =
                    repository.getOne(id)
            }.onFailure { throw ResultIndicatorNotFoundException() }
        return resultIndicatorEntity
    }
}

@Repository
interface ResultIndicatorRepository : JpaRepository<ResultIndicatorEntity, Long>, CustomResultIndicatorRepository {

    @EntityGraph(attributePaths = ["programmePriorityPolicyEntity.programmePriority"])
    override fun findAll(pageable: Pageable): Page<ResultIndicatorEntity>

    @EntityGraph(attributePaths = ["programmePriorityPolicyEntity.programmePriority"])
    override fun findById(id: Long): Optional<ResultIndicatorEntity>


    fun findTop50ByOrderById(): List<ResultIndicatorEntity>
    fun findOneByIdentifier(identifier: String): ResultIndicatorEntity?
    fun findAllByProgrammePriorityPolicyEntityProgrammeObjectivePolicyOrderById(programmeObjectivePolicy: ProgrammeObjectivePolicy): Iterable<ResultIndicatorEntity>
}
