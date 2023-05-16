package io.cloudflight.jems.server.nuts.repository

import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface NutsRegion3Repository : PagingAndSortingRepository<NutsRegion3, String> {

    fun findAllByIdIn(ids: List<String>): Set<NutsRegion3>
}
