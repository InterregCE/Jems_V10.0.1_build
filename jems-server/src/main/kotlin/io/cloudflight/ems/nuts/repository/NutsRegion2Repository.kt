package io.cloudflight.ems.nuts.repository

import io.cloudflight.ems.nuts.entity.NutsRegion2
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface NutsRegion2Repository : PagingAndSortingRepository<NutsRegion2, String>
