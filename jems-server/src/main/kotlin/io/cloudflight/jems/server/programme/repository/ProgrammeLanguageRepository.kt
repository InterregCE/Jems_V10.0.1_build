package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import io.cloudflight.jems.server.programme.entity.ProgrammeLanguage
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeLanguageRepository : CrudRepository<ProgrammeLanguage, SystemLanguage>
