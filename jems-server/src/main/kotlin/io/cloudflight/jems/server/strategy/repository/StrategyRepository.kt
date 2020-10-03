package io.cloudflight.jems.server.strategy.repository

import io.cloudflight.jems.api.strategy.ProgrammeStrategy
import io.cloudflight.jems.server.strategy.entity.Strategy
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StrategyRepository : PagingAndSortingRepository<Strategy, ProgrammeStrategy>
