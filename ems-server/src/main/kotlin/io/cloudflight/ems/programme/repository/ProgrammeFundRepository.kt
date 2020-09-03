package io.cloudflight.ems.programme.repository

import io.cloudflight.ems.programme.entity.ProgrammeFund
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeFundRepository : CrudRepository<ProgrammeFund, Long>
