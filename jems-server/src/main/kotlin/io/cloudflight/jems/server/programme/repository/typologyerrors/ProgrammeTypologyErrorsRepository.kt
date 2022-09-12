package io.cloudflight.jems.server.programme.repository.typologyerrors

import io.cloudflight.jems.server.programme.entity.typologyerrors.ProgrammeTypologyErrorsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeTypologyErrorsRepository : JpaRepository<ProgrammeTypologyErrorsEntity, Long>
