package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.server.programme.entity.Strategy
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StrategyRepository : PagingAndSortingRepository<Strategy, ProgrammeStrategy>
