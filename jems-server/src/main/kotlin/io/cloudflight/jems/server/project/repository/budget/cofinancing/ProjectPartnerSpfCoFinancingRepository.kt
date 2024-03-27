package io.cloudflight.jems.server.project.repository.budget.cofinancing

import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerFinancingRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PerPartnerSpfFinancingRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingFundId
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingSpfEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectPartnerSpfCoFinancingRepository : JpaRepository<ProjectPartnerCoFinancingSpfEntity, ProjectPartnerCoFinancingFundId> {

    fun deleteByCoFinancingFundIdPartnerId(partnerId: Long)

    fun findAllByCoFinancingFundIdPartnerId(partnerId: Long): MutableList<ProjectPartnerCoFinancingSpfEntity>

    @Query(
        """
            SELECT
             financing.*,
             financing.order_nr AS orderNr,
             fund.id AS fundId,
             fund.type AS fundType,
             fund.selected AS selected,
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

    @Query("""
        SELECT
            partner.project_id AS projectId,
            spfCoFin.partner_id AS partnerId,
            programmeFund.id AS fundId,
            programmeFund.type AS type,
            programmeFundTransl.language AS language,
            programmeFundTransl.abbreviation AS abbreviation
        FROM project_partner_co_financing_spf FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS spfCoFin
            LEFT JOIN project_partner FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS partner ON spfCoFin.partner_id = partner.id
            LEFT JOIN programme_fund programmeFund ON spfCoFin.programme_fund_id = programmeFund.id
            LEFT JOIN programme_fund_transl programmeFundTransl ON programmeFundTransl.source_entity_id = programmeFund.id
        WHERE partner.project_id = :projectId AND spfCoFin.programme_fund_id IS NOT NULL
        ORDER BY projectId, partnerId, fundId
    """, nativeQuery = true)
    fun findSpfFundsPerPartner(projectId: Long, timestamp: Timestamp): List<PerPartnerSpfFinancingRow>

}
