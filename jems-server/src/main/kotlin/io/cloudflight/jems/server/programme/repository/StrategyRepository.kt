package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StrategyRepository : PagingAndSortingRepository<ProgrammeStrategyEntity, ProgrammeStrategy> {

    fun getAllByStrategyInAndActiveTrue(strategies: Set<ProgrammeStrategy>): Iterable<ProgrammeStrategyEntity>

}
