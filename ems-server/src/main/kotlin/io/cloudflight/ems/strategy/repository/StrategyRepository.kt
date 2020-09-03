package io.cloudflight.ems.strategy.repository

import io.cloudflight.ems.strategy.entity.Strategy
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StrategyRepository : PagingAndSortingRepository<Strategy, Long> {
}
