package io.cloudflight.ems.strategy.repository

import io.cloudflight.ems.api.strategy.ProgrammeStrategy
import io.cloudflight.ems.strategy.entity.Strategy
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StrategyRepository : PagingAndSortingRepository<Strategy, ProgrammeStrategy> {
}
