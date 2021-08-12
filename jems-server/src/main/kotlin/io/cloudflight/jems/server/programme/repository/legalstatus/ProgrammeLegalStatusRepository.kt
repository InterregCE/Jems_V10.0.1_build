package io.cloudflight.jems.server.programme.repository.legalstatus

import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.ProgrammeLegalStatusNotFoundException
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface CustomProgrammeLegalStatusRepository {
    fun getReferenceIfExistsOrThrow(id: Long?): ProgrammeLegalStatusEntity
}

open class CustomProgrammeLegalStatusRepositoryImpl(val repository: ProgrammeLegalStatusRepository) :
    CustomProgrammeLegalStatusRepository {
    @Transactional(readOnly = true)
    override fun getReferenceIfExistsOrThrow(id: Long?): ProgrammeLegalStatusEntity =
        runCatching { repository.getOne(id!!) }.onFailure { throw ProgrammeLegalStatusNotFoundException() }.getOrThrow()
}

@Repository
interface ProgrammeLegalStatusRepository : JpaRepository<ProgrammeLegalStatusEntity, Long>,
    CustomProgrammeLegalStatusRepository {

    @EntityGraph(value = "ProgrammeLegalStatusEntity.fetchWithTranslations")
    fun findTop20ByOrderById(): Iterable<ProgrammeLegalStatusEntity>

    @EntityGraph(value = "ProgrammeLegalStatusEntity.fetchWithTranslations")
    override fun findAllById(ids: Iterable<Long>): List<ProgrammeLegalStatusEntity>

    fun findByTypeIn(types: List<ProgrammeLegalStatusType>): List<ProgrammeLegalStatusEntity>
}
