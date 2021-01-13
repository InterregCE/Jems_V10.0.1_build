package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetUnitCostEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ProjectPartnerBudgetUnitCostRepository : CrudRepository<ProjectPartnerBudgetUnitCostEntity, Long> {

    fun findAllByPartnerIdOrderByIdAsc(partnerId: Long): List<ProjectPartnerBudgetUnitCostEntity>

    @Query("SELECT SUM(e.rowSum) FROM #{#entityName} e WHERE e.partnerId = :id")
    fun sumTotalForPartner(@Param("id") partnerId: Long): BigDecimal?

    @Query("SELECT new io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEntity(e.partnerId, SUM(e.rowSum)) FROM #{#entityName} e WHERE e.partnerId IN :ids GROUP BY e.partnerId")
    fun sumForAllPartners(@Param("ids") partnerIds: Set<Long>): List<ProjectPartnerBudgetEntity>

    fun deleteAllByPartnerId(partnerId: Long)

    fun deleteAllByPartnerIdAndIdNotIn(partnerId: Long, idList: List<Long>)

}
