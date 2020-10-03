package io.cloudflight.jems.server.nuts.repository

import io.cloudflight.jems.server.nuts.entity.NutsRegion1
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface NutsRegion1Repository : PagingAndSortingRepository<NutsRegion1, String>
