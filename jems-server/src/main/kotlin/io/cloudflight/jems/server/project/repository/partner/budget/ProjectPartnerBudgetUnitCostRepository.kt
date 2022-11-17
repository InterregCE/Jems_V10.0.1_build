package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectUnitCostRow
import java.sql.Timestamp
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

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

    @Query(
        """
            SELECT
                entity.id AS id,
                cost.id AS costId,
                cost.cost_per_unit AS pricePerUnit,
                entity.number_of_units AS numberOfUnits,
                cost_transl.name AS name,
                cost_transl.description AS description,
                cost_transl.type AS unitType,
                cost_transl.language AS language
                FROM #{#entityName} AS entity
                LEFT JOIN programme_unit_cost AS cost ON cost.id = entity.programme_unit_cost_id
                LEFT JOIN programme_unit_cost_transl AS cost_transl ON cost.id = cost_transl.programme_unit_cost_id
                WHERE entity.partner_id in :partnerIds
        """,
        nativeQuery = true
    )
    fun findProjectUnitCosts(partnerIds: Set<Long>): List<ProjectUnitCostRow>

    @Query(
        """
            SELECT
                entity.id AS id,
                cost.id AS costId,
                cost.cost_per_unit AS pricePerUnit,
                entity.number_of_units AS numberOfUnits,
                cost_transl.name AS name,
                cost_transl.description AS description,
                cost_transl.type AS unitType,
                cost_transl.language AS language
                FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP  :timestamp AS entity
                LEFT JOIN programme_unit_cost AS cost ON cost.id = entity.programme_unit_cost_id
                LEFT JOIN programme_unit_cost_transl AS cost_transl ON cost.id = cost_transl.programme_unit_cost_id
                WHERE entity.partner_id in :partnerIds
        """,
        nativeQuery = true
    )
    fun findProjectUnitCostsAsOfTimestamp(partnerIds: Set<Long?>, timestamp: Timestamp): List<ProjectUnitCostRow>

    fun existsByUnitCostId(unitCostId: Long): Boolean

}
