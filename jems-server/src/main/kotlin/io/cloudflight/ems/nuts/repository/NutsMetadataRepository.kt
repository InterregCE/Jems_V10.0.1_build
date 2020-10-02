package io.cloudflight.ems.nuts.repository

import io.cloudflight.ems.nuts.entity.NutsMetadata
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface NutsMetadataRepository : PagingAndSortingRepository<NutsMetadata, Long>
