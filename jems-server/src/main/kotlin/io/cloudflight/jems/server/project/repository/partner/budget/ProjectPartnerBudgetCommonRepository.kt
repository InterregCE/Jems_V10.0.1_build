package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.CommonBudget
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

@NoRepositoryBean
interface ProjectPartnerBudgetCommonRepository<T : CommonBudget> : CrudRepository<T, Long> {

    fun findAllByPartnerIdOrderByIdAsc(partnerId: Long): List<T>

    @Query("SELECT SUM(e.budget.rowSum) FROM #{#entityName} e WHERE e.partnerId = :id")
    fun sumTotalForPartner(@Param("id") partnerId: Long): BigDecimal?

    @Query("SELECT new io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEntity(e.partnerId, SUM(e.budget.rowSum)) FROM #{#entityName} e WHERE e.partnerId IN :ids GROUP BY e.partnerId")
    fun sumForAllPartners(@Param("ids") partnerIds: Set<Long>): List<ProjectPartnerBudgetEntity>

    fun deleteAllByPartnerId(partnerId: Long)

}
