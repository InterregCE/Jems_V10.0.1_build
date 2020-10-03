package io.cloudflight.jems.server.nuts.repository

import io.cloudflight.jems.server.nuts.entity.NutsMetadata
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface NutsMetadataRepository : PagingAndSortingRepository<NutsMetadata, Long>
