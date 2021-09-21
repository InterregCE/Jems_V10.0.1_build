package io.cloudflight.jems.server.programme.repository.translation

import io.cloudflight.jems.server.programme.entity.translation.TranslationFileEntity
import io.cloudflight.jems.server.programme.entity.translation.TranslationFileId
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface TranslationFileRepository : PagingAndSortingRepository<TranslationFileEntity, TranslationFileId>
