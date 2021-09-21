package io.cloudflight.jems.server.programme.repository.stateaid

import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface CustomProgrammeStateAidRepository {
    fun getReferenceIfExistsOrThrow(id: Long?): ProgrammeStateAidEntity?
}

open class CustomProgrammeStateAidRepositoryImpl(val repository: ProgrammeStateAidRepository) :
    CustomProgrammeStateAidRepository {
    @Transactional(readOnly = true)
    override fun getReferenceIfExistsOrThrow(id: Long?): ProgrammeStateAidEntity? {
        var programmeStateAidEntity: ProgrammeStateAidEntity? = null
        if (id != null)
            runCatching {
                programmeStateAidEntity =
                    repository.getOne(id)
            }.onFailure { throw ProgrammeStateAidNotFoundException() }
        return programmeStateAidEntity
    }
}

@Repository
interface ProgrammeStateAidRepository : JpaRepository<ProgrammeStateAidEntity, Long>, CustomProgrammeStateAidRepository {

    @EntityGraph(value = "ProgrammeStateAidEntity.fetchWithTranslations")
    fun findAllByOrderById(): Iterable<ProgrammeStateAidEntity>

    @EntityGraph(value = "ProgrammeStateAidEntity.fetchWithTranslations")
    override fun findAllById(ids: Iterable<Long>): List<ProgrammeStateAidEntity>
}
