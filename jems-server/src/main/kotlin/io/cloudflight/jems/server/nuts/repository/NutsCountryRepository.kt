package io.cloudflight.jems.server.nuts.repository

import io.cloudflight.jems.server.nuts.entity.NutsCountry
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface NutsCountryRepository : PagingAndSortingRepository<NutsCountry, String>
