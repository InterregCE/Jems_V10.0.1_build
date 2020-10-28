package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.CommonBudget
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import java.math.BigDecimal

@NoRepositoryBean
interface ProjectPartnerBudgetCommonRepository<T : CommonBudget> : CrudRepository<T, Long> {

    fun findAllByPartnerIdOrderByIdAsc(partnerId: Long): List<T>

    @Query("SELECT SUM(e.budget.rowSum) FROM #{#entityName} e WHERE e.partnerId = ?1")
    fun sumTotalForPartner(partnerId: Long): BigDecimal?

}
