package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostRow
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectPartnerBudgetUnitCostRepository :
    ProjectPartnerBaseBudgetRepository<ProjectPartnerBudgetUnitCostEntity> {

    @Query(
        """
            SELECT
             entity.*,
             period.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP  :timestamp AS entity
             LEFT JOIN #{#entityName}_period FOR SYSTEM_TIME AS OF TIMESTAMP  :timestamp AS period ON entity.id =  period.budget_id
             WHERE  entity.partner_id = :partnerId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findAllByPartnerIdAsOfTimestamp(partnerId: Long, timestamp: Timestamp): List<ProjectPartnerBudgetUnitCostRow>
}
