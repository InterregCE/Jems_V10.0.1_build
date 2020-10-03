package io.cloudflight.jems.server.repository

import io.cloudflight.jems.server.entity.ProgrammeData
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeDataRepository : CrudRepository<ProgrammeData, Long>
