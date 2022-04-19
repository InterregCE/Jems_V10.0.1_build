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
             entity.*,
             COUNT(instance.id) AS count
             FROM #{#entityName} AS entity
             LEFT JOIN checklist_instance AS instance ON entity.id = instance.programme_checklist_id
             GROUP by entity.id
             ORDER BY entity.id ASC LIMIT 100
             """,
        nativeQuery = true
    )
    fun findTop100ByOrderById(): Iterable<ProgrammeChecklistRow>

    fun findByType(checklistType: ProgrammeChecklistType): Iterable<IdNamePair>

}
