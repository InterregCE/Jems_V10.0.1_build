package io.cloudflight.ems.nuts.repository

import io.cloudflight.ems.nuts.entity.NutsCountry
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface NutsCountryRepository : PagingAndSortingRepository<NutsCountry, String>
