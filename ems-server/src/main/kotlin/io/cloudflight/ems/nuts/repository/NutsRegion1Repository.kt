package io.cloudflight.ems.nuts.repository

import io.cloudflight.ems.nuts.entity.NutsRegion1
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface NutsRegion1Repository : PagingAndSortingRepository<NutsRegion1, String>
