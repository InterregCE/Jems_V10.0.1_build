package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.ProgrammeData
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeDataRepository : CrudRepository<ProgrammeData, Long>
