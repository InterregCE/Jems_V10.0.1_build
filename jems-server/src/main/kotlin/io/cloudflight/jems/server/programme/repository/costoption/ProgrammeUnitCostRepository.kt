package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeUnitCostRepository : JpaRepository<ProgrammeUnitCostEntity, Long> {

    fun findTop100ByOrderById(): Iterable<ProgrammeUnitCostEntity>

}
