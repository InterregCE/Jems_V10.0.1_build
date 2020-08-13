package io.cloudflight.ems.nuts.repository

import io.cloudflight.ems.nuts.entity.ProgrammeNutsRegionSaveEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeNutsSaveRepository : JpaRepository<ProgrammeNutsRegionSaveEntity, String>
