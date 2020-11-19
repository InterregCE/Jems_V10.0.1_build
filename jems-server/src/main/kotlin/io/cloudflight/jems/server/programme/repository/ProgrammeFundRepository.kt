package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.server.programme.entity.ProgrammeFundEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeFundRepository : CrudRepository<ProgrammeFundEntity, Long>
