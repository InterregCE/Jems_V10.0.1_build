package io.cloudflight.jems.server.project.repository.budget.cofinancing

import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerFinancingRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingFundId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectPartnerCoFinancingRepository : JpaRepository<ProjectPartnerCoFinancingEntity, ProjectPartnerCoFinancingFundId> {

    fun deleteByCoFinancingFundIdPartnerId(partnerId: Long)

    fun findAllByCoFinancingFundIdPartnerId(partnerId: Long): MutableList<ProjectPartnerCoFinancingEntity>

    @Query(
        """
            SELECT
             financing.*,
             fund.id AS fundId,
             fund.type AS fundType,
             fund.selected AS selected,
             fund.order_nr AS orderNr,
             fundTransl.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS financing
             LEFT JOIN programme_fund AS fund ON financing.programme_fund_id = fund.id
             LEFT JOIN programme_fund_transl AS fundTransl ON fund.id = fundTransl.source_entity_id
             WHERE financing.partner_id = :partnerId
             """,
        nativeQuery = true
    )
    fun findPartnerFinancingByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerFinancingRow>


}
