package io.cloudflight.ems.nuts.repository

import io.cloudflight.ems.nuts.entity.NutsRegion3
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface NutsRegion3Repository : PagingAndSortingRepository<NutsRegion3, String> {

    @Query(
        value = "SELECT * FROM nuts_region_3 WHERE id IN (SELECT nuts_region_3_id FROM programme_nuts)",
        nativeQuery = true
    )
    fun findAllProgrammeNutsRegions(): Set<NutsRegion3>

}
