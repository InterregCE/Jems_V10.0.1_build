package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetBase
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetView
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.sql.Timestamp

@NoRepositoryBean
interface ProjectPartnerBaseBudgetRepository<T : ProjectPartnerBudgetBase> : CrudRepository<T, Long> {

    fun findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId: Long): List<T>

    @Query(
        """
            SELECT
             entity.*,
             translation.*,
             period.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP  :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP  :timestamp AS translation ON entity.id = translation.source_entity_id
             LEFT JOIN #{#entityName}_period FOR SYSTEM_TIME AS OF TIMESTAMP  :timestamp AS period ON entity.id =  period.budget_id
             WHERE  entity.partner_id = :partnerId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun <T> findAllByPartnerIdAsOfTimestamp(partnerId: Long, timestamp: Timestamp, viewClass: Class<T>): List<T>

    @Query("SELECT SUM(e.baseProperties.rowSum) FROM #{#entityName} e WHERE e.baseProperties.partnerId = :id")
    fun sumTotalForPartner(@Param("id") partnerId: Long): BigDecimal?

    @Query("SELECT new io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetView(e.baseProperties.partnerId, SUM(e.baseProperties.rowSum)) FROM #{#entityName} e WHERE e.baseProperties.partnerId IN :ids GROUP BY e.baseProperties.partnerId")
    fun sumForAllPartners(@Param("ids") partnerIds: Set<Long>): List<ProjectPartnerBudgetView>

    fun deleteAllByBasePropertiesPartnerId(partnerId: Long)

    fun deleteAllByBasePropertiesPartnerIdAndIdNotIn(partnerId: Long, idList: Set<Long>)
}
