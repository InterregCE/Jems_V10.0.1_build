package io.cloudflight.ems.programme.repository

import io.cloudflight.ems.programme.entity.ProgrammeLegalStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeLegalStatusRepository : CrudRepository<ProgrammeLegalStatus, Long>
