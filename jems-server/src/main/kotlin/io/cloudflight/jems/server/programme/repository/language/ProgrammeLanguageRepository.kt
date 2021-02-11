package io.cloudflight.jems.server.programme.repository.language

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.programme.entity.language.ProgrammeLanguageEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeLanguageRepository : CrudRepository<ProgrammeLanguageEntity, SystemLanguage> {

    fun findTop40ByOrderByCode(): Iterable<ProgrammeLanguageEntity>

}
