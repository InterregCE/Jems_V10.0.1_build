package io.cloudflight.jems.server.programme.repository.checklist

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistRow
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeChecklistRepository : JpaRepository<ProgrammeChecklistEntity, Long> {

    @Query(
        """
            SELECT DISTINCT
                new io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistRow(
                    entity.id,
                    entity.type,
                    entity.name,
                    entity.minScore,
                    entity.maxScore,
                    entity.allowsDecimalScore,
                    entity.lastModificationDate,
                    COUNT(instance.id)
                )
             FROM #{#entityName} AS entity
             LEFT JOIN checklist_instance AS instance ON entity.id = instance.programmeChecklist.id
             GROUP by entity.id
             """
    )
    fun findTop100ByOrderByIdDesc(): Iterable<ProgrammeChecklistRow>

    fun findByType(checklistType: ProgrammeChecklistType): Iterable<IdNamePair>

}
