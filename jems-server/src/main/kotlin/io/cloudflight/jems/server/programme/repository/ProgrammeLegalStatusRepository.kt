package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeLegalStatusRepository : CrudRepository<ProgrammeLegalStatus, Long>
